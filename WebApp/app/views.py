from flask import render_template, flash, redirect, session, request, url_for, make_response, jsonify, jsonify,Response
from flask_admin.contrib.sqla import ModelView
from sqlalchemy.sql.functions import user
from werkzeug.exceptions import abort
from flask_login import current_user, login_user, logout_user, LoginManager, login_required
from app import app, db, admin, login_manager
from .forms import LoginForm, RegisterForm, valid_login, EditProfileForm, MovieInfoForm, TopUpForm, MovieDataForm, \
    ResetForm, OrderForm, CommentForm
from .models import User, Movie, Order, Comment
from werkzeug.utils import secure_filename
import os
import datetime
import random
import json
basedir = os.path.abspath(os.path.dirname(__file__))
ALLOWED_EXTENSIONS = {'png', 'jpg', 'JPG', 'PNG', 'bmp'}
admin.add_view(ModelView(User, db.session))
admin.add_view(ModelView(Movie, db.session))

dict = {'0': '0', '1': '1', '2': '6', '3': '4', '4': '3', '5': '9', '6': '2', '7': '8', '8': '7', '9': '5'}


def encryption(user_id, seat_number, movie_id):
    movie_id = movie_id % 100086
    maplist = ''
    for i in str(movie_id):
        maplist += dict[i]
    user_id = user_id % 43
    seat_number = seat_number % 27
    randomlist = ['z', 'y', 'x', 'w', 'v', 'u', 't', 's', 'r', 'q', 'p', 'o', 'n', 'm', 'l', 'k', 'j', 'i', 'h', 'g',
                  'f', 'e', 'd', 'c', 'b', 'a', 'Z', 'Y', 'X', 'W', 'V', 'U', 'T', 'S', 'R', 'Q', 'P', 'O', 'N', 'M',
                  'L', 'K', 'J', 'I', 'H', 'G', 'F', 'E', 'D', 'C', 'B', 'A']
    code = (''.join(random.sample(randomlist, random.randint(0, 3)))) + str(user_id) + (
            ''.join(random.sample(randomlist, random.randint(1, 3))) + str(seat_number)) + (
               ''.join(random.sample(randomlist, random.randint(2, 4)))) + maplist
    code += ''.join(random.sample(randomlist, 24 - len(code)))
    return code


def get_price(origin_price):
    if current_user.user_vip_level == 0:
        return origin_price
    elif current_user.user_vip_level == 1:
        return int(origin_price * 0.9)
    elif current_user.user_vip_level == 2:
        return int(origin_price * 0.8)
    elif current_user.user_vip_level == 3:
        return int(origin_price * 0.7)


@login_manager.user_loader
def load_user(user_id):
    return User.query.get(int(user_id))


def create_uuid(filename):
    now_time = datetime.datetime.now().strftime("%Y%m%d%H%M%S")
    random_num = random.randint(0, 100)
    if random_num <= 10:
        random_num = str(0) + str(random_num)
    unique_num = str(now_time) + str(random_num)
    return unique_num + filename


def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1] in ALLOWED_EXTENSIONS


@app.route('/', methods=['GET', 'POST'])
def home():
    movies = Movie.query.order_by(Movie.score.desc()).group_by(Movie.movie_name)[1:9]
    movs = Movie.query.order_by(Movie.score.desc()).group_by(Movie.movie_name).limit(1)
    lastmovs = Movie.query.order_by(Movie.premiere_date.desc()).group_by(Movie.movie_name).limit(10)
    recomovs = Movie.query.order_by(Movie.score.desc()).group_by(Movie.movie_name).limit(10)
    session['search_options'] = ''
    return render_template('home.html', movies=movies, movs=movs, lastmovs=lastmovs, recomovs=recomovs)


@app.route('/register', methods=['GET', 'POST'])
def register():
    form = RegisterForm()
    # Verify success, create user information, jump to the login page
    if form.validate_on_submit():
        user = User(user_name=form.username.data, mail=form.email.data, password=form.password.data, user_vip_level=0,
                    money=0)
        # user.set_password(form.password.data)
        db.session.add(user)
        db.session.commit()
        login_user(user)
        next = request.args.get('next')
        if next is None or not next.startswith('/'):
            next = url_for('home')
        return redirect(next)
    return render_template('register.html', form=form, title='REGISTER')


@app.route('/login', methods=['GET', 'POST'])
def login():
    # Load the login Form Form
    form = LoginForm()
    if form.validate_on_submit():
        user = User.query.filter(User.mail == form.email.data, User.password == form.password.data).first()
        if user:
            login_user(user)
            return redirect(url_for('home'))
        else:
            flash('The password is wrong ')

    return render_template('login.html', form=form, title='LOGIN')


@app.route('/logout', methods=['GET', 'POST'])
@login_required
def logout():
    logout_user()
    return redirect(url_for('home'))


@app.route('/change-password', methods=['GET', 'POST'])
@login_required  # 只有登录的人才能修改密码
def change_password():
    form = ResetForm()
    if form.validate_on_submit():
        if (current_user.password == form.old_password.data):
            # 这里引入user的上下文，这个概念不太懂，暂且当成全局变量来用
            current_user.password = form.new_password.data
            # 修改密码
            db.session.add(current_user)
            db.session.commit()
            # 加入数据库的session，这里不需要.commit()，在配置文件中已经配置了自动保存
            # flash('Your password has been updated.')
            return redirect(url_for('profile', user_id=current_user.user_id))
        else:
            flash('Invalid password.')
    return render_template("change_password.html", form=form)


@app.route('/poster_upload', methods=['GET', 'POST'])
@login_required
def poster_upload():
    form = MovieInfoForm()
    if form.validate_on_submit():
        new_movie = Movie(movie_name=form.movie_name.data, movie_type=form.movie_type.data,
                          movie_length=form.movie_length.data, introduction=form.introduction.data,
                          country=form.country.data, special_effect=form.special_effect.data,
                          comment=form.comments.data, actors=form.actors.data,
                          director=form.director.data, premiere_date=form.premiere_date.data,
                          score=form.score.data)
        if request.method == 'POST':
            filename = form.poster.data.filename
            filename = create_uuid(filename)
            upload_path = os.path.join(basedir, 'static/images', secure_filename(filename))
            form.poster.data.save(upload_path)
            new_movie.poster = filename
            db.session.add(new_movie)
            db.session.commit()
            flash('Uploaded successfully!')

    return render_template('upload.html', form=form)


@app.route('/edit_movieInfo/<id>', methods=['GET', 'POST'])
@login_required
def edit_movieInfo(id):
    movie = Movie.query.get(id)
    form = MovieInfoForm(obj=movie)
    if form.validate_on_submit():
        if request.method == 'POST':
            movie.movie_name = form.movie_name.data
            movie.movie_type = form.movie_type.data
            movie.movie_length = form.movie_length.data
            movie.introduction = form.introduction.data
            movie.country = form.country.data
            movie.special_effect = form.special_effect.data
            movie.comment = form.comments.data
            movie.actors = form.actors.data
            movie.director = form.director.data
            movie.premiere_date = form.premiere_date.data
            movie.score = form.score.data
            filename = form.poster.data.filename
            filename = create_uuid(filename)
            upload_path = os.path.join(basedir, 'static/images', secure_filename(filename))
            form.poster.data.save(upload_path)
            movie.poster = filename
            db.session.commit()
        return redirect(url_for('admin_list'))
    return render_template('edit_movieInfo.html', form=form, movie=movie)


@app.route('/edit_movieData/<id>', methods=['GET', 'POST'])
@login_required
def edit_movieData(id):
    movie = Movie.query.get(id)
    form = MovieDataForm(obj=movie)
    if form.validate_on_submit():
        if request.method == 'POST':
            movie.date = form.date.data
            movie.start_time = form.finish_time.data
            movie.finish_time = form.finish_time.data
            movie.scene = form.scene.data
            movie.projection_hall = form.projection_hall.data
            movie.price = form.price.data
            db.session.commit()
        return redirect(url_for('show_scenes',movie_name= movie.movie_name))
    return render_template('edit_movieData.html', form=form, movie=movie)


@app.route('/add_scene/<id>', methods=['GET', 'POST'])
@login_required
def add_scene(id):
    movie = Movie.query.get(id)
    infoForm = MovieInfoForm(obj=movie)
    dataForm = MovieDataForm()
    if dataForm.validate_on_submit():
        movie_instance = Movie(movie_name=infoForm.movie_name.data, movie_type=infoForm.movie_type.data,
                               movie_length=infoForm.movie_length.data, introduction=infoForm.introduction.data,
                               country=infoForm.country.data, special_effect=infoForm.special_effect.data,
                               comment=infoForm.comments.data, actors=infoForm.actors.data,
                               director=infoForm.director.data, premiere_date=infoForm.premiere_date.data,
                               score=infoForm.score.data, poster=infoForm.poster.data,

                               date=dataForm.date.data, start_time=dataForm.start_time.data,
                               finish_time=dataForm.finish_time.data, scene=dataForm.scene.data,
                               projection_hall=dataForm.projection_hall.data, price=dataForm.price.data,
                               )
        db.session.add(movie_instance)
        db.session.commit()
        return redirect(url_for('admin_list'))
    return render_template('add_scene.html', form=dataForm)


@app.route('/show_scenes/<movie_name>', methods=['GET', 'POST'])
@login_required
def show_scenes(movie_name):
    movies = Movie.query.filter_by(movie_name=movie_name).order_by(db.desc(Movie.scene))

    return render_template('show_scenes.html', movies=movies)


@app.route('/delete_scene/<id>', methods=['GET', 'POST'])
@login_required
def delete_scene(id):
    movie = Movie.query.get(id)
    name = movie.movie_name
    db.session.delete(movie)
    db.session.commit()

    return redirect(url_for('show_scenes', movie_name=name))


@app.route('/profile/<user_id>')
def profile(user_id):
    user = User.query.filter_by(user_id=user_id).first()
    if user is None:
        abort(404)
    return render_template('profile.html', title='Individual Center', user=user)


@app.route('/users', methods=['GET', 'POST'])
def users():
    users = User.query.filter().all()

    return render_template('profile_trial.html', users=users)


@app.route('/list')
def list():
    movies = Movie.query.order_by(Movie.score.desc()).group_by(Movie.movie_name).limit(15)
    return render_template('list.html', title='LIST', movies=movies)


@app.route('/admin_list', methods=['GET', 'POST'])
def admin_list():
    movies = Movie.query.all()
    return render_template('admin_list.html', title='LIST', movies=movies)


@app.route('/edit_profile/<user_id>', methods=['GET', 'POST'])
@login_required
def edit(user_id):
    person = User.query.get(user_id)
    form = EditProfileForm(obj=person)
    if form.validate_on_submit():
        if request.method == 'POST':
            person.age = form.age.data
            person.gender = form.gender.data
            person.phone_number = form.phone_number.data
            filename = form.avatar.data.filename
            filename = create_uuid(filename)
            upload_path = os.path.join(basedir, 'static/images', secure_filename(filename))
            form.avatar.data.save(upload_path)
            person.avatar = filename
            db.session.commit()
        return redirect(url_for('profile', user_id=person.user_id))
    return render_template('edit.html', title='Edit', form=form, person=person)


@app.route('/detail/<movie_id>')
def detail(movie_id):
    movie = Movie.query.filter_by(movie_id=movie_id).first()
    if movie is None:
        abort(404)
    return render_template('detail.html', title='Movie Details', movie=movie)


@app.route('/order/<movie_id>', methods=['GET', 'POST'])
def order(movie_id):
    movie = Movie.query.filter_by(movie_id=movie_id).first()
    true_price = get_price(movie.price)
    if movie is None:
        abort(404)
    seats = []
    for i in range(len(movie.serial_number)):
        seats.append(int(movie.serial_number[i]))
    return render_template('order.html', title='Order ticket', movie=movie, seats=seats, price=true_price)


@app.route('/order_operation/<movie_id>', methods=['GET', 'POST'])
def order_operation(movie_id):
    seats = []
    movie = Movie.query.filter_by(movie_id=movie_id).first()
    if movie is None:
        abort(404)
    seat_number = request.form.get('number')
    seat_number = int(seat_number) - 1
    movie_id=int(movie_id)
    if int(movie.serial_number[seat_number]) == 0:
        print("qifei")
        movie.serial_number = movie.serial_number[:seat_number]+"1"+movie.serial_number[seat_number+1:]
        ticket_key = encryption(current_user.user_id, seat_number + 1, movie_id)
        order = Order(order_date=datetime.date.today(), seat_number=seat_number+1, order_user=current_user.user_id,
                      ticket_key=ticket_key, order_movie=movie.movie_id)
        true_price = get_price(movie.price)
        current_user.money = current_user.money - true_price
        db.session.add(order)
        db.session.commit()
    for i in range(len(movie.serial_number)):
        seats.append(int(movie.serial_number[i]))

    return render_template('order.html', title='Order ticket', movie=movie, seats=seats, price=true_price)


@app.route('/balance/<user_id>', methods=['GET', 'POST'])
def balance(user_id):
    form = TopUpForm()
    if form.validate_on_submit():
        number = form.number.data
        current_user.money += number
        db.session.commit()

    return render_template('balance.html', title='balance', form=form, money=current_user.money)


@app.route('/search_movie/<value>', methods=['GET', 'POST'])
def search_movie(value):
    number = Movie.query.filter_by(movie_name=value).count()
    if number >= 1:
        movies = Movie.query.filter_by(movie_name=value)
    else:
        movies = Movie.query.filter(Movie.movie_name.contains(value))
        if movies is None:
            flash('There is no such movie.')
            next = request.args.get('next')
            if next is None or not next.startswith('/'):
                next = url_for('home')
            return redirect(next)
    return render_template('list.html', title='LIST', movies=movies, number=number)


@app.route('/search_user/<value>', methods=['GET', 'POST'])
def search_user(value):
    print("芜湖芜湖")
    print(value)
    user = User.query.filter_by(user_name=value).first()
    if user == None:
        flash('There is no such user.')
        next = request.args.get('next')
        if next is None or not next.startswith('/'):
            next = url_for('home')
        return redirect(next)
    return render_template('profile.html', title=user.user_name, user=user)


@app.route('/search', methods=['GET', 'POST'])
def search():
    value = request.form.get('value')
    if value is None:
        value = session['search_options']
    else:
        if session['search_options'] is None:
            value = ''
        else:
            session['search_options'] = value
    if value[-2:] == "-u":
        return redirect(url_for('search_user', value=value[0:-2].strip()))
    if value[-2:] == "-m":
        return redirect(url_for('search_movie', value=value[0:-2].strip()))
    number = Movie.query.filter_by(movie_name=value).count()
    if number != 0:
        return redirect(url_for('search_movie', value=value))
    else:
        number = User.query.filter_by(user_name=value).count()
        if number != 0:
            return redirect(url_for('search_user', value=value))
        else:
            number = Movie.query.filter(Movie.movie_name.contains(value)).count()
            if number != 0:
                if value == '':
                    return redirect(url_for('list'))
                return redirect(url_for('search_movie', value=value))
            else:
                next = request.args.get('next')
                if next is None or not next.startswith('/'):
                    next = url_for('home')
                return redirect(next)


@app.route('/comment/<movie_id>', methods=['GET','POST'])
def comment(movie_id):
    movie = Movie.query.get_or_404(movie_id)
    form = CommentForm()
    if form.validate_on_submit():
        comment = Comment(body=form.body.data, movies=movie,
                            users=current_user._get_current_object())
        db.session.add(comment)
        db.session.commit()
        return redirect(url_for('detail', movie_id=movie.movie_id))
    comments = movie.comments.order_by(Comment.comment_id.desc()).all()
    return render_template('comment.html', movie=movie, form=form, comments=comments)

# view functions for android app
@app.route('/appnet/register', methods=['GET', 'POST'])  
def app_regist():
    resp = {
                "error_code": 0,
                "message": "User name already exists!"
                }
    resp1={
                "error_code": 2,
                "message": "Register successfully!"
                }
    if request.form['username'] and request.form['password']:
        for user in User.query.all():
            if user.user_name==request.form['username']:
                app.logger.error("User used a existing username!")
                return jsonify(resp)
        new_user = User(user_name=request.form['username'], mail=request.form['email'], password=request.form['password'] )
        db.session.add(new_user)
        db.session.commit()
        return jsonify(resp1)


@app.route('/appnet/login', methods=['GET', 'POST'])  
def app_login():
    resp = {
                "error_code": 0,
                "message": "No such user name!"
                }
    resp1={
                "error_code": 1,
                "message": "Wrong password!"
                }
    if request.form['username'] and request.form['password']:
        for user in User.query.all():
            if user.user_name==request.form['username']:
                if user.password==request.form['password']:
                    if user.user_vip_level==10:
                        return jsonify(resp)
                    resp2={
                        "error_code": 2,
                        "message": "Log in successfully!"
                    }
                    return jsonify(resp2)
                else:
                    return jsonify(resp1)
        return jsonify(resp)

@app.route('/appnet/get_profile', methods=['GET', 'POST'])  
def app_get_profile():
    if request.form['username']:
        user=User.query.filter(User.user_name==request.form['username']).first()
        resp={
            "gender": user.gender,
            "age": user.age,
            "email": user.mail,
            "phonenumber": user.phone_number
        }
        return jsonify(resp)

@app.route('/appnet/get_vip_level', methods=['GET', 'POST'])  
def app_get_vip_level():
    if request.form['username']:
        user=User.query.filter(User.user_name==request.form['username']).first()
        resp={
            "gender": user.gender,
            "vip_level": user.user_vip_level
        }
        return jsonify(resp)

@app.route('/appnet/top_up', methods=['GET', 'POST'])  
def app_top_up():
    if request.form['username']:
        user=User.query.filter(User.user_name==request.form['username']).first()
        if request.form['amountCode']==1:
            user.money=user.money+100
            user.accumulated_amount=user.accumulated_amount+100
        elif request.form['amountCode']==2:
            user.money=user.money+200
            user.accumulated_amount=user.accumulated_amount+200
        else:
            user.money=user.money+500
            user.accumulated_amount=user.accumulated_amount+500
        db.session.commit()
        if user.accumulated_amount<200:
            user.user_vip_level=1
        elif user.accumulated_amount<500:
            user.user_vip_level=2
        else:
            user.user_vip_level=3
        db.session.commit()
        resp={
            "Flag":"1"
        }
        return jsonify(resp)
  
@app.route('/img/<img_name>', methods=['GET', 'POST'])  #input the image name as url, then this function would return the image
def get_img(img_name):
    
    #newname="20181116214144575.png"
    filename='static/images/'+str(img_name)
    img_path =os.path.join(os.path.dirname(os.path.realpath(__file__)),filename )
    with open(img_path, 'rb') as f:
        image = f.read()
    response = make_response(image)
    response.headers['Content-Type'] = 'image/png'
    resp = Response(image, mimetype="image/png")
    return resp

@app.route('/appnet/movie_info', methods=['GET', 'POST'])
def app_get_movieinfo():
    movies = Movie.query.filter().all()
    json_list="["
    i=0
    for movie in movies:
        json_info={
            "movie_id":movie.movie_id,
            "img_url":movie.poster,
            "name":movie.movie_name,
            "release_data":str(movie.premiere_date),
            "length":movie.movie_length,
            "score":movie.score,
            "special_effect":movie.special_effect,
            "actors":movie.actors,
            "movie_type":movie.movie_type,
            "introduction":movie.introduction,
            "comments":movie.comment,
            "country":movie.country,
            "director":movie.director,
            "date":str(movie.date),
            "start_time":str(movie.start_time),
            "finish_time":str(movie.finish_time),
            "scene":movie.scene,
            "projection_hall":movie.projection_hall,
            "price":movie.price,
            "cinemas":movie.cinemas,
            "serial_number":movie.serial_number
            }
          
        movie_info =json.dumps(json_info)
        if i>=1:
            json_list+=","
        json_list=json_list+movie_info
        i+=1
        
    json_list+="]"
    resp = Response(json_list, mimetype="application/json") 
    return resp
        
        
@app.route('/appnet/search', methods=['GET', 'POST'])
def app_search():
    if request.form['key_word']:
        movies = Movie.query.filter().all()
        json_list="["
        i=0
        for movie in movies:
            if request.form['key_word'] in movie.movie_name:
                json_info={
                    "img_url":"",
                    "name":movie.movie_name,
                    "release_data":str(movie.premiere_date),
                    "length":movie.movie_length,
                    "score":movie.score,
                    "special_effect":movie.special_effect,
                    "actors":movie.actors,
                     }
                movie_info =json.dumps(json_info)
                if i>=1:
                    json_list+=","
                json_list=json_list+movie_info
                i+=1
            
        json_list+="]"
        print(json_list)
        resp = Response(json_list, mimetype="application/json") 
        return resp
    
@app.route('/appnet/edit_profile', methods=['GET', 'POST'])
def app_edit_profile():
    if request.form['loginUsername']:
        user=User.query.filter(User.user_name==request.form['loginUsername']).first()
        user.user_name=request.form['username']
        user.gender=request.form['gender']
        user.age=request.form['age']
        user.mail=request.form['email']
        user.phone_number=request.form['phonenumber']
        db.session.commit()
        resp={
            "Flag":"1"
        }
        return jsonify(resp)

@app.route('/appnet/get_membership', methods=['GET', 'POST'])  
def app_get_membership():
    if request.form['username']:
        user=User.query.filter(User.user_name==request.form['username']).first()
        resp={
            "gender": user.gender,
            "vip_level": user.user_vip_level,
            "balance": user.money,
            "accumulation": user.accumulated_amount
        }
        return jsonify(resp)
  
@app.route('/appnet/select_seat', methods=['GET', 'POST'])
def app_select_seat():
    if request.form['seat_nums']:
        flag=0
        nums=request.form['seat_nums'].split('.')
        movies =Movie.query.filter_by(movie_id=request.form['id']).first()
        resp1={
            "result": "Seats are selected",
            "errorcode":0,
            "serial_number":movies.serial_number
        }
        seat=str(movies.serial_number)
        seats=list(seat)
        #critical area
        for num in nums:
            if seats[int(num)]=='1':
                flag=1
                return jsonify(resp1)
        
        for num in nums:
            seats[int(num)]='1'
        new_seats=''.join(seats)
        movies.serial_number=new_seats
        db.session.commit()
        resp2={
            "result": "Success",
            "errorcode":1,
            "serial_number":new_seats
        }
        print(new_seats)
        return jsonify(resp2)
        

@app.route('/appnet/generate_order', methods=['GET', 'POST'])
def app_generate_order():
    if request.form['seat_nums']:
        flag=0
        nums=request.form['seat_nums'].split('.')
        movies =Movie.query.filter_by(movie_id=request.form['id']).first()
        orders=Order.query.filter_by(order_movie=request.form['id'],seat_number=int(nums[0])).all()
        resp1={
            "result": "order exist",
            "errorcode":0,
            "serial_number":movies.serial_number
        }
        if(len(orders)!=0):
            return jsonify(resp1)
        
        resp2={
            "result": "order generated",
            "errorcode":1,
            "serial_number":movies.serial_number
        }
        user=User.query.filter_by(user_name=request.form['user_name']).first()
        user_id=user.user_id
        movie_id=movies.movie_id
        for num in nums:
            seat_number=int(num)
            ticket_key = encryption(user_id, seat_number + 1, movie_id)
            order = Order(order_date=datetime.date.today(), seat_number=seat_number+1, order_user=user_id,
                          ticket_key=ticket_key, order_movie=movie_id)
            true_price = get_price(user,movies.price)
            user.money = user.money - true_price
            db.session.add(order)
        db.session.commit()
        return jsonify(resp2)
        
@app.route('/appnet/cancel_order', methods=['GET', 'POST'])
def app_cancel_order():
    if request.form['seat_nums']:
        movies =Movie.query.filter_by(movie_id=request.form['id']).first()
        seat=str(movies.serial_number)
        seats=list(seat)
        nums=request.form['seat_nums'].split('.')
        for num in nums:
                seats[int(num)]='0'
        new_seats=''.join(seats)
        movies.serial_number=new_seats
        db.session.commit()
        resp2={
        "result": "order canceled"
        }
        return jsonify(resp2)
        
        
def get_price(user,origin_price):
    if user.user_vip_level == 0:
        return origin_price
    elif user.user_vip_level == 1:
        return int(origin_price * 0.9)
    elif user.user_vip_level == 2:
        return int(origin_price * 0.8)
    elif user.user_vip_level == 3:
        return int(origin_price * 0.7)

from flask import render_template, flash, redirect, session, request, url_for, make_response, jsonify,Response
from flask_admin.contrib.sqla import ModelView
from sqlalchemy.sql.functions import user
from werkzeug.exceptions import abort
from flask_login import current_user, login_user, logout_user, LoginManager, login_required
from app import app, db, admin, login_manager
from .forms import LoginForm, RegisterForm, valid_login
from .models import User, Movie
import os
# import requests
from werkzeug.utils import secure_filename
import datetime
import random
import json


basedir = os.path.abspath(os.path.dirname(__file__))
ALLOWED_EXTENSIONS = {'png', 'jpg', 'JPG', 'PNG', 'bmp'}
admin.add_view(ModelView(User, db.session))
admin.add_view(ModelView(Movie, db.session))

@login_manager.user_loader
def load_user(user_id):
        return User.query.get(int(user_id))

def create_uuid():
    now_time = datetime.datetime.now().strftime("%Y%m%d%H%M%S")
    random_num = random.randint(0, 100)
    if random_num <= 10:
        random_num = str(0) + str(random_num)
    unique_num = str(now_time) + str(random_num)
    return unique_num


def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1] in ALLOWED_EXTENSIONS


@app.route('/', methods=['GET', 'POST'])
def home():
    movies = Movie.query.all()
    movs = Movie.query.filter_by(movie_id=1).all()
    return render_template('home.html',movies=movies,movs=movs)


@app.route('/register', methods=['GET', 'POST'])
def register():
    form = RegisterForm()
    # Verify success, create user information, jump to the login page
    if form.validate_on_submit():
        user = User(user_name=form.username.data, mail=form.email.data, password=form.password.data)
        # user.set_password(form.password.data)
        db.session.add(user)
        db.session.commit()
        flash('Congratulations, you are now a registered user！')
        return redirect(url_for('login'))
    return render_template('register.html', form=form,title='REGISTER')


@app.route('/login', methods=['GET', 'POST'])
def login():
 # Load the login Form Form
    form = LoginForm()
    if valid_login(form.email.data, form.password.data):
        flash("Login Successfully")
        #session['mail'] = form.emile.data
        user=User.query.filter_by(mail=form.email.data).first()
        login_user(user)
        next = request.args.get('next')
        if next is None or not next.startswith('/'):
            next = url_for('home')
        return redirect(next)
    else:
        flash('Incorrect email or password')
    return render_template('login.html', form=form,title='LOGIN')


@app.route('/test', methods=['GET', 'POST'])
def test():
    users = User.query.filter().all()
    users_number = User.query.filter().count()
    movies = Movie.query.filter().all()
    movies_number = Movie.query.filter().count()
    return render_template('test.html', users=users, users_number=users_number, movies=movies,
                           movies_number=movies_number)


@app.route('/images', methods=['GET', 'POST'])
def images():
    movies = Movie.query.filter().all()
    return render_template('images.html', movies=movies)


@app.route('/poster_upload', methods=['GET', 'POST'])
def poster_upload():
    return render_template('upload.html')


@app.route('/upload_poster', methods=['GET', 'POST'])
def upload_poster():
    f = request.files['file']
    if not (f and allowed_file(f.filename)):
        return render_template('upload.html', allow=2)
    filename = create_uuid() + '.jpg'
    upload_path = os.path.join(basedir, 'static/images', secure_filename(filename))
    f.save(upload_path)

    return render_template('upload.html', allow=1)


@app.route('/upload_poster<id>', methods=['GET', 'POST'])
def upload_poster_id():
    movie = Movie.query.filter(id=id).first()
    f = request.files['file']
    if not (f and allowed_file(f.filename)):
        return render_template('upload.html', allow=2)
    filename = create_uuid() + '.jpg'
    upload_path = os.path.join(basedir, 'static/images', secure_filename(filename))
    f.save(upload_path)
    movie.poster=filename

    return render_template('upload.html', allow=1)


@app.route('/profile/<user_id>')
def profile(user_id):
    user=User.query.filter_by(user_id=user_id).first()
    if user is None:
        abort(404)

    return render_template('profile.html', user=user)


# @app.route('/profile', methods=['GET', 'POST'])
# def profile():
#     user = User.query.filter_by( user_id=1 ).first()
#     if user is None:
#         abort(404)
#
#     return render_template('profile.html', user=user)


@app.route('/users', methods=['GET', 'POST'])
def users():
    users = User.query.filter().all()

    return render_template('profile_trial.html', users=users)


@app.route('/list')
def list():
    movies = Movie.query.all()
    return render_template('list.html', title='LIST',movies=movies)


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
            "cinemas":movie.cinemas
            }
          
        movie_info =json.dumps(json_info)
        print(movie_info)
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



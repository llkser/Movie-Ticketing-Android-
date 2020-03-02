from flask import render_template, flash, session,redirect,url_for,request
from os.path import join, dirname, realpath
import os
from app import app,db
from .forms import UserForm,goodsForm,checkForm,checkForm2
from . import models
import datetime
from datetime import timedelta
import json
import random
from flask import make_response,jsonify
import celery
from flask_wtf.csrf import CsrfProtect
@app.route('/statu')
def statu():
    return render_template('news.html')
@celery.task(bind=True)
def long_task(self):
    """Background task that runs a long function with progress reports."""
    verb = ['Starting up', 'Booting', 'Repairing', 'Loading', 'Checking']
    adjective = ['master', 'radiant', 'silent', 'harmonic', 'fast']
    noun = ['solar array', 'particle reshaper', 'cosmic ray', 'orbiter', 'bit']
    message = ''
    total = random.randint(10, 50)
    for i in range(total):
        if not message or random.random() < 0.25:
            message = '{0} {1} {2}...'.format(random.choice(verb),
                                              random.choice(adjective),
                                              random.choice(noun))
        self.update_state(state='PROGRESS',
                          meta={'current': i, 'total': total,
                                'status': message})
        time.sleep(1)
    return {'current': 100, 'total': 100, 'status': 'Task completed!',
            'result': 42}

@app.route('/longtask', methods=['POST'])
def longtask():
    task = long_task.apply_async()
    return jsonify({}), 202, {'Location': url_for('taskstatus',
                                                  task_id=task.id)}
@app.route('/status/<task_id>')
def taskstatus(task_id):
    task = long_task.AsyncResult(task_id)
    if task.state == 'PENDING':
        response = {
            'state': task.state,
            'current': 0,
            'total': 1,
            'status': 'Pending...'
        }
    elif task.state != 'FAILURE':
        response = {
            'state': task.state,
            'current': task.info.get('current', 0),
            'total': task.info.get('total', 1),
            'status': task.info.get('status', '')
        }
        if 'result' in task.info:
            response['result'] = task.info['result']
    else:
        # something went wrong in the background job
        response = {
            'state': task.state,
            'current': 1,
            'total': 1,
            'status': str(task.info),  # this is the exception raised
        }
    return jsonify(response)



app.config['PERMANENT_SESSION_LIFETIME']=timedelta(days=7)

db.create_all()
@app.route('/regist', methods=['GET', 'POST'])
def regist():
    form = UserForm()
    form.name.data= request.form.get("name")
    form.gender.data= request.form.get("gender")
    form.trader.data= request.form.get("trader")
    form.password.data= request.form.get("password")
    form.email.data= request.form.get("email")
    
    if form.validate():
        for p in models.User.query.all():
            if p.name==form.name.data :
                return jsonify(code=3, message='This name has been used')
            elif p.email==form.email.data:
                return jsonify(code=4, message='This email has been used')
            '''
        if form.trader.data=='T':
            p =models.User(name=form.name.data,gender=form.gender.data, email=form.email.data, password=form.password.data ,trader=True )
        else:
            p =models.User(name=form.name.data,gender=form.gender.data, email=form.email.data, password=form.password.data ,trader=False )
        session.permanent=True
        session['user']=form.name.data#试试todolist
        session['trader']=p.trader
        session['id']=p.id
        db.session.add(p)
        db.session.commit()
        flash('Welcome %s'%(form.gender.data))
        '''
        return jsonify(code=1, message='Success')
    else:
        resp = {
            "code": 2,
            "message": form.get_errors()
        }
        return jsonify(resp)
    return render_template('register.html',title='Regist',form=form, trader=session.get('trader'))

@app.route('/login', methods=['GET', 'POST'])
def login():
    form = checkForm()
    form.name.data= request.form.get("name")
    form.password.data= request.form.get("password")
    form.email.data= request.form.get("email")
    if form.validate():
        for p in models.User.query.all():
            if p.name==form.name.data:
                if p.email!=form.email.data:
                    return jsonify(code=5, message='The email can not match user name')
                elif p.password!=form.password.data:
                    return jsonify(code=6, message='The password can not match user name')
                else:
                    session['user']=form.name.data
                    session['trader']=p.trader
                    session['id']=p.id
                    return jsonify(code=1, message='Success')
        return jsonify(code=7, message='No such user name')
    else:
        resp = {
            "code": 2,
            "message": form.get_errors()
        }
        return jsonify(resp)
    return render_template('login.html',form=form, trader=session.get('trader'))
    

@app.route('/show_all')
def show_all():
   return render_template('show_all.html', Users = models.User.query.all() )
'''
@app.route('/show_all2')
def show_all2():
   return render_template('main.html', Users = models.todolists.query.all() )
   '''
@app.route('/main')
def main():
    if not (models.User.query.all() and session.get('user')):
        return redirect(url_for('regist'))
    if len(models.User.query.filter_by(trader=True).all())!=0:
        num=len(models.goods.query.filter(models.goods.User_id!=session.get('id'),models.goods.saled==False).group_by('name','User_id','description','url').all())
        rang=num
        if num>12:
            rang=12
        rs=random.sample(range(0,num),rang)
        randoms=[models.goods.query.filter(models.goods.User_id!=session.get('id'),models.goods.saled==False).group_by('name','User_id','description','url')[x] for x in rs]

        i=0
        total=[]
        while i<5:
            if i==0:
                cc='Electronics'
            if i==1:
                cc='Book'
            if i==2:
                cc='Game'
            if i==3:
                cc='Handmade'
            if i==4:
                cc='Sport'
            num1=len(models.goods.query.filter(models.goods.User_id!=session.get('id'),models.goods.classify==cc,models.goods.saled==False).group_by('name','User_id','description','url').all())
            rang1=num1
            if num1>4:
                rang1=4
            rs1=random.sample(range(0,num1),rang1)
            
            if i==0:
                randoms0=[models.goods.query.filter(models.goods.User_id!=session.get('id'),models.goods.saled==False,models.goods.classify==cc).group_by('name','User_id','description','url')[x] for x in rs1]
                if(len(randoms0)>0):
                    total.append(randoms0)
            if i==1:
                randoms1=[models.goods.query.filter(models.goods.User_id!=session.get('id'),models.goods.saled==False,models.goods.classify==cc).group_by('name','User_id','description','url')[x] for x in rs1]
                if(len(randoms1)>0):
                    total.append(randoms1)
            if i==2:
                randoms2=[models.goods.query.filter(models.goods.User_id!=session.get('id'),models.goods.saled==False,models.goods.classify==cc).group_by('name','User_id','description','url')[x] for x in rs1]
                if(len(randoms2)>0):
                    total.append(randoms2)
            if i==3:
                randoms3=[models.goods.query.filter(models.goods.User_id!=session.get('id'),models.goods.saled==False,models.goods.classify==cc).group_by('name','User_id','description','url')[x] for x in rs1]
                if(len(randoms3)>0):
                    total.append(randoms3)
            if i==4:
                randoms4=[models.goods.query.filter(models.goods.User_id!=session.get('id'),models.goods.saled==False,models.goods.classify==cc).group_by('name','User_id','description','url')[x] for x in rs1]
                if(len(randoms4)>0):
                    total.append(randoms4)
            i+=1
        return render_template('main.html', Users =models.User.query.filter(models.User.trader==True,models.User.name!=session.get('user')).all()
                               ,id=session.get('user'),trader=session.get('trader'),randoms=randoms,i=0,total=total)
    
    return render_template('main.html', message = 'Your todo-list is empty now, please create a task',id=session.get('user'),trader=session.get('trader') )
'''
        num2=len(models.goods.query.filter(models.goods.User_id!=session.get('id'),models.goods.classify=='Book').group_by('name','User_id','description','url').all())
        rang2=num2
        if num2>4:
            rang2=4
        rs2=random.sample(range(0,num2),rang2)
        randoms2=[models.goods.query.filter(models.goods.User_id!=session.get('id'),models.goods.saled==False,models.goods.classify=='Book').group_by('name','User_id','description','url')[x] for x in rs2]

        num3=len(models.goods.query.filter(models.goods.User_id!=session.get('id'),models.goods.classify=='Game').group_by('name','User_id','description','url').all())
        rang3=num3
        if num3>4:
            rang3=4
        rs3=random.sample(range(0,num3),rang3)
        randoms3=[models.goods.query.filter(models.goods.User_id!=session.get('id'),models.goods.saled==False,models.goods.classify=='Game').group_by('name','User_id','description','url')[x] for x in rs3]

        num4=len(models.goods.query.filter(models.goods.User_id!=session.get('id'),models.goods.classify=='Game').group_by('name','User_id','description','url').all())
        rang4=num3
        if num4>4:
            rang4=4
        rs4=random.sample(range(0,num3),rang3)
        randoms4=[models.goods.query.filter(models.goods.User_id!=session.get('id'),models.goods.saled==False,models.goods.classify=='Game').group_by('name','User_id','description','url')[x] for x in rs3]
       ''' 
'''
def main():
    if not (models.User.query.all() and session.get('user')):
        return redirect(url_for('regist'))
    if models.User.query.all():
        tasklist=[]
        for p in models.User.query.all():
            if p.name==session.get('user'):
                if p.todolist.count()!=0:
                    return render_template('main.html', Users = p.todolist,id=p.name )
    return render_template('main.html', message = 'Your todo-list is empty now, please create a task',id=session.get('user') )
  '''  

@app.route('/create',methods=['GET', 'POST'] )
def create():
    if not (models.User.query.all() and session.get('user')):
        return redirect(url_for('regist'))
    p=models.User.query.filter_by(name=session.get('user'))[0]
    if p.trader==False:
        return redirect(url_for('main'))
    form = goodsForm()
    if form.validate_on_submit():
            num=form.number.data
            filename = form.file.data.filename
            paths=join(dirname(realpath(__file__)),"static/photo" )
            path_list=os.listdir(paths)
            ii=0
            newname=filename
            while newname in path_list:
                newname=str(ii)+'_'+filename
                ii+=1

            filename='static/photo/'+newname
            urls='photo/'+newname
            form.file.data.save(join(dirname(realpath(__file__)),filename ))
            
            for i in range(num):
                p1=models.goods(name=form.name.data,User_id=p.id,description=form.description.data,url=urls,price=form.price.data,classify=form.classify.data)
                
                db.session.add(p1)
                
                db.session.commit()
                p2=models.log(time=datetime.datetime.now(),userid=p.id,action='Trader '+p.name+' add a good named '+form.name.data,goodid=p1.id)
                db.session.add(p2)
                db.session.commit()
            return redirect(url_for('goodcontrol'))
    return render_template('create.html',form=form, trader=session.get('trader'))






@app.route('/respond', methods=['POST'])
def respond():
    data = json.loads(request.data.decode("utf-8"))
    response = data.get('response')
    print(1)
    tasklist='<table  cellspacing="10" style="margin:auto">'+'<thead><tr><th>Name</th><th>pic</th><th>description</th><th>price</th></tr></thead>'
    for p in models.goods.query.filter(models.goods.User_id!=session.get('id'),models.goods.saled==False).group_by('name','User_id','description','url','price'):
        if response in p.name:
            tasklist=(tasklist+'<tr><th>'+p.name+"</th><th><img src="+url_for('static', filename=p.url )+" width='80px' height='80px' /></th><th>"+p.description+'</th><th>'+str(p.price)+'</th><th><a href=/buygood/'
                      +str(p.id)+"><button >buy goods</button></a></th></tr>")
    tasklist=tasklist+'</table>'
    return json.dumps({'status': 'OK', 'response': tasklist})

@app.route('/respond2', methods=['POST'])
def respond2():
    data = json.loads(request.data.decode("utf-8"))
    response = data.get('response')
    changeid=int(response)
    for p in models.todolists.query.all():
        if changeid==p.id:
            if p.finish==None:
                p.finish=True
            elif p.finish==False:
                p.finish=True
            elif p.finish==True:
                p.finish=False
        db.session.commit()
    return json.dumps({'status': 'OK', 'response': changeid})

@app.route('/respond3', methods=['POST'])
def respond3():
    data = json.loads(request.data.decode("utf-8"))
    response = data.get('response')
    changeid=int(response)
    aaa=''
    for p in models.todolists.query.all():
        if changeid==p.id:
            aaa=aaa+p.task+' '+str(p.deadline)+' '+p.description
            return json.dumps({'status': 'OK', 'response': aaa})

@app.route('/search', methods=['GET', 'POST'])
def search():
    if not (len(models.User.query.all())!=0 and session.get('user')):
        return redirect(url_for('regist'))
    return render_template('search.html')


@app.route('/finished')
def finished():
    if not (models.User.query.all() and session.get('user')):
        return redirect(url_for('regist'))
    if models.User.query.all():
        for p in models.User.query.all():
            if p.name==session.get('user'):
                if p.todolist:
                    return render_template('finished.html', Users = models.todolists.query.filter_by(finish=True,User_id=p.id) )
    return render_template('finished.html', message = 'Your todo-list is empty now, please create a task' )


@app.route('/unfinished')
def unfinished():
    if not (models.User.query.all() and session.get('user')):
        return redirect(url_for('regist'))
    if models.User.query.all():
        for p in models.User.query.all():
            if p.name==session.get('user'):
                if p.todolist:
                    return render_template('unfinished.html', Users = models.todolists.query.filter_by(finish=False,User_id=p.id) )
    return render_template('unfinished.html', message = 'Your todo-list is empty now, please create a task' )


@app.route('/delete')
def delete():
    for p in models.goods.query.all():
        db.session.delete(p)
    for p in models.record.query.all():
        db.session.delete(p)
    db.session.commit()
    return redirect(url_for('goodcontrol'))
'''
@app.route('/edit/<ids>')
def edit(ids):
    form = todolistForm()
    ids=int(ids)
    for p in models.todolists.query.all():
        if p.id==ids:
            if form.validate_on_submit():
                p.task=form.task.data
                p.deadline=str(form.deadliney.data)+'-'+str(form.deadlinem.data)+'-'+str(form.deadlined.data)
                p.description=form.description.data
                db.session.commit()
                return redirect(url_for('main'))
            return render_template('main.html', Users =models.todolists.query.filter_by(User_id=p.User_id) ,id=session.get('user'),form=form)
        
'''
@app.route('/good/<idd>')#mode buygood for user and trader
def good(idd):
    if not (models.User.query.all() and session.get('user')):
        return redirect(url_for('regist'))
    if len(models.User.query.filter_by(id=idd).all())==0 or models.User.query.filter_by(id=idd)[0].trader==False:
        return redirect(url_for('main'))
    return render_template('goodscontrol.html', Users =models.User.query.filter_by(id=idd)[0].good.group_by('name','User_id','description','url'),mode='buygood')

@app.route('/goodcontrol',methods=['GET', 'POST'] )#mode goodcontrol for trader
def goodcontrol():
    if not (models.User.query.all() and session.get('user')):
        return redirect(url_for('regist'))
    if session.get('trader')==True:
        return render_template('goodscontrol.html', Users =models.User.query.filter_by(name=session.get('user'))[0].good.group_by('name','User_id','description','url'),mode='goodcontrol')
    else:
        return redirect(url_for('main'))
'''
@app.route('/buygood/<ids>')
def buygood(ids):
    aa=models.goods.query.filter_by(id=ids)[0]
    aa.saled=True
    a=aa.User_id
    b=models.User.query.filter_by(name=session.get('user'))[0].id
    models.goods.query.filter_by(id=ids)[0].User_id=b
    p1=models.record(goodid=ids,traderid=a,userid=b)
    db.session.add(p1)
    db.session.commit()
    return redirect(url_for('good',idd=a))
'''

@app.route('/buygood/<ids>',methods=['POST','GET'])
def buygood1(ids):
    form=checkForm2()
    good=models.goods.query.filter_by(id=ids)[0]
    goodlist=models.goods.query.filter_by(User_id=good.User_id,name=good.name,url=good.url)
    remain=len(goodlist.all())
    b=models.User.query.filter_by(name=session.get('user'))[0].id
    if form.validate_on_submit():
        if form.number.data > remain:
            return render_template('goodsale.html', good=good,remain=str(remain),error='Purchase quantity exceeds inventory ',form=form)
        for i in range(form.number.data):
            idd=goodlist[0].id
            tid=goodlist[0].User_id
            p2=models.log(time=datetime.datetime.now(),userid=b,action='User '+session.get('user')+' buy a good named '+goodlist[0].name+' from trader with id '+str(tid),goodid=idd)
            goodlist[0].saled=True
            goodlist[0].User_id=b
            
            p1=models.record(goodid=idd,traderid=tid,userid=b)
            db.session.add(p2)
            db.session.add(p1)
            db.session.commit()
        return redirect(url_for('goodlist'))
    return render_template('goodsale.html', good=good,remain=str(remain),form=form )

@app.route('/record')
def record():
    return render_template('record.html', Users =models.record.query.all() )
@app.route('/log')
def log():
    return render_template('log.html', Users =models.log.query.filter_by().order_by(models.log.userid,models.log.time) )

@app.route('/goodlist',methods=['GET', 'POST'] )
def goodlist():
    return render_template('goodscontrol.html', Users =models.User.query.filter_by(name=session.get('user'))[0].good,mode='refunds' )

@app.route('/refunds/<idss>',methods=['GET', 'POST'] )
def refunds(idss):
    
    rec=models.record.query.filter_by(goodid=idss)[0]
    p2=models.log(time=datetime.datetime.now(),userid=session.get('id'),action='User '+session.get('user')+' refund a good named '+models.goods.query.filter_by(id=idss)[0].name+' to trader with id '+str(rec.traderid),goodid=idss)
    models.goods.query.filter_by(id=idss)[0].saled=False
    models.goods.query.filter_by(id=idss)[0].User_id=rec.traderid
    db.session.delete(models.record.query.filter_by(goodid=idss)[0])
    db.session.add(p2)
    db.session.commit()
    return redirect(url_for('goodlist'))
if __name__ == '__main__':
    app.run()

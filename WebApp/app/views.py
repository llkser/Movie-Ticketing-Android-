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
import socket
import uuid
db.create_all()
    
@app.route('/appnet/register', methods=['GET', 'POST'])  
def app_regist():
    resp = {
                "error_code": 0,
                "message": "User name already exists"
                }
    resp1={
                "error_code": 2,
                "message": "registering successfully"
                }
    if request.form['username'] and request.form['password']:
        for user in models.User.query.all():
            if user.name==request.form['username']:
                app.logger.error("user use a username existed")
                return jsonify(resp)
        new_user =models.User(name=request.form['username'],gender='Male', email="http-test", password=request.form['password'] ,trader=False )
        db.session.add(new_user)
        db.session.commit()
        return jsonify(resp)
    return jsonify(resp1)

@app.route('/appnet/login', methods=['GET', 'POST'])  
def app_regist():
    resp = {
                "error_code": 0,
                "message": "No such user name"
                }
    resp1={
                "error_code": 1,
                "message": "wrong password"
                }
    resp2={
                "error_code": 2,
                "message": "log in successfully"
                }
    if request.form['username'] and request.form['password']:
        for user in models.User.query.all():
            if user.name==request.form['username']:
                if user.password==request.form['password']:
                    return jsonify(resp2)
                else:
                    return jsonify(resp1)
        return jsonify(resp)


@app.route('/img/<img_name>', methods=['GET', 'POST'])  #input the image name as url, then this function would return the image
def get_img(img_name):
    from flask import Response
    #newname="20181116214144575.png"
    filename='static/photo/'+str(img_name)
    img_path =join(dirname(realpath(__file__)),filename )
    with open(img_path, 'rb') as f:
        image = f.read()
    resp = Response(image, mimetype="image/jpeg")
    return resp




from flask_wtf import FlaskForm
from wtforms import IntegerField,TextField,RadioField,FileField,SelectField,PasswordField
from wtforms.validators import DataRequired,NumberRange,Length
from wtforms.fields.html5 import DateField
from flask_wtf.file import FileRequired,FileAllowed

    
    


class UserForm(FlaskForm):
    name = TextField('Name', validators=[DataRequired('Please enter your name!'),Length(max=25)])
    gender = RadioField('Gender', choices = [('Male','Male'),('Female','Female')])
    trader = RadioField('Trader', choices = [('T','trader'),('F','user')])
    password=PasswordField('Password', validators=[DataRequired('Please enter your name!'),Length(max=25)])
    email= TextField('Email', validators=[DataRequired('please enter your email'),Length(max=25)])
    remember = RadioField('Remember me', choices = [('T','yes'),('F','no')],render_kw={"onclick" : "test4();"})
    

class goodsForm(FlaskForm):
    name = TextField('Task', validators=[DataRequired('Please enter your task!'),Length(max=25)])
    description=TextField('Description', validators=[DataRequired('Please enter your task!'),Length(max=60)])
    classify=SelectField('Type', choices = [('Game','Game'),('Electronics','Electronics'),('Handmade','Handmade'),('Book','Book'),('Sport','Sport')])
    file = FileField(label="picture",validators=[FileRequired(),FileAllowed(['jpg', 'png'])])
    number=IntegerField('number of goods', validators=[NumberRange(min=1, max=200)])
    price=IntegerField('price', validators=[NumberRange(min=0, max=200000)])

class checkForm(FlaskForm):
    name = TextField('Name', validators=[DataRequired('Please enter your name!'),Length(max=25)])
    email= TextField('Email', validators=[DataRequired('please enter your email'),Length(max=25)])
    password=PasswordField('Password', validators=[DataRequired('Please enter your name!'),Length(max=25)])
    remember = RadioField('Remember me', choices = [('T','yes'),('F','no')],render_kw={"onclick" : "test4();"})

class checkForm2(FlaskForm):
    number=IntegerField('number of goods you want to buy', validators=[DataRequired('Please enter your number!')])

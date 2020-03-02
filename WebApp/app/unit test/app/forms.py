from flask_wtf import FlaskForm
from wtforms import IntegerField,TextField,RadioField,FileField,SelectField,Form
from wtforms.validators import DataRequired,NumberRange,Length
from wtforms.fields.html5 import DateField
from flask_wtf.file import FileRequired,FileAllowed

    
    


class UserForm(Form):
    name = TextField('Name', validators=[DataRequired(message='User name is empty'),Length(max=25,message='User name is out of range')])
    gender = RadioField('Gender', choices = [('Male','Male'),('Female','Female')])
    trader = RadioField('Trader', choices = [('T','trader'),('F','user')])
    password=TextField('Password', validators=[DataRequired(message='User passward is empty'),Length(max=25,message='User password is out of range')])
    email= TextField('Email', validators=[DataRequired(message='User email is empty'),Length(max=25,message='User email is out of range')])

    def get_errors(self):
        errors = ''
        for v in self.errors.values():
            for m in v:
                errors += m
            errors += '\n'
        return errors

    

class goodsForm(Form):
    name = TextField('Task', validators=[DataRequired('Please enter your task!'),Length(max=25)])
    description=TextField('Description', validators=[DataRequired('Please enter your task!'),Length(max=60)])
    classify=SelectField('Type', choices = [('Game','Game'),('Electronics','Electronics'),('Handmade','Handmade'),('Book','Book'),('Sport','Sport')])
    file = FileField(label="picture",validators=[FileRequired(),FileAllowed(['jpg', 'png'])])
    number=IntegerField('number of goods', validators=[NumberRange(min=1, max=200)])
    price=IntegerField('price', validators=[NumberRange(min=0, max=200000)])

class checkForm(Form):
    name = TextField('Name', validators=[DataRequired(message='User name is empty'),Length(max=25,message='User name is out of range')])
    email= TextField('Email', validators=[DataRequired(message='User passward is empty'),Length(max=25,message='User email is out of range')])
    password=TextField('Password', validators=[DataRequired(message='User email is empty'),Length(max=25,message='User password is out of range')])
    
    def get_errors(self):
        errors = ''
        for v in self.errors.values():
            for m in v:
                errors += m
            errors += '\n'
        return errors

class checkForm2(FlaskForm):
    number=IntegerField('number of goods you want to buy', validators=[DataRequired('Please enter your number!')])

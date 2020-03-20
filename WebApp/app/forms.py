from flask_wtf import Form
from wtforms import StringField, BooleanField, SelectField, PasswordField, \
    SubmitField, IntegerField, RadioField, TextAreaField
from wtforms.fields.html5 import DateField, TimeField
from wtforms.validators import DataRequired, ValidationError, EqualTo, Length, Email
from .models import User
from sqlalchemy import or_,and_

class LoginForm(Form):
    email = StringField('email', render_kw={'placeholder': 'email'}, validators=[Email(message='Invalid mailbox format')])
    password = StringField(render_kw={'placeholder': 'email'}, validators=[DataRequired()])


class RegisterForm(Form):
    username = StringField('username', render_kw={'placeholder': 'username'},
                           validators=[DataRequired(message='The username cannot be empty'), Length(6, 32, message='User names can only be between 6 and 32 characters')])
    password = PasswordField('password', render_kw={'placeholder': 'password'},
                             validators=[DataRequired(message='The password cannot be empty'), Length(6, 32, message='password can only be between 6 and 32 characters')])
    password_confirm = PasswordField('password-confirm', validators=[EqualTo('password', message='The two passwords do not match')])
    email = StringField('email', validators=[Email(message='Invalid mailbox format')])
    submit = SubmitField('Register now')

    def validate_username(self, username):
        user = User.query.filter_by(user_name=username.data).first()
        if user is not None:
            raise ValidationError('The user name has been registered')


class ResetForm(Form):
    old_password = PasswordField('old_password', render_kw={'placeholder': 'old_password'},
                                 validators=[DataRequired(message='Please enter the old password')])
    new_password = PasswordField('new_password', render_kw={'placeholder': 'new_password'},
                                 validators=[DataRequired(message='Please enter the new password'),
                                             Length(min=6)])
    new_password_confirm = PasswordField('Repeat new password', render_kw={'placeholder': 'Repeat new password'},
                                         validators=[
                                             DataRequired(message='Please make sure the passwords are the same'),
                                             EqualTo('new_password')])
    submit = SubmitField('Reset now')


def valid_login(email, password):
    user = User.query.filter(and_(User.mail == email, User.password == password)).first()
    if user:
        return True
    else:
        return False

# class UserInfoForm(Form):
#     username = StringField('username', validators=[DataRequired()])
#     avatar = FileField('avatar')
#     age = IntegerField('age', validators=[DataRequired()])
#     gender = RadioField('gender', choices=[('male', 'Male'), ('female', 'Female')], validators=[DataRequired()])
#     phone_number = IntegerField('phone number', validators=[DataRequired(), Length(11)])
#     vip_level = IntegerField('vip level', validators=[DataRequired()])
#     submit = SubmitField('submit')
#     def validate_username(self, username):
#         user = User.query.filter_by(User_name=username.data).first()
#         if user is not None:
#             raise ValidationError('Username already exists!')
#
# #forms used by manager
# class ManagerLoginForm(Form):
#     username = StringField('manager account',validators=[DataRequired(message='Please enter your username')])
#     password = PasswordField('password',validators=[DataRequired(message='Please enter your password')])
#     submit = SubmitField('login')
#
# class MovieInfoForm(Form):
#     movie_name = StringField('movie name', validators=[DataRequired()])
#     movie_type = StringField('movie type', validators=[DataRequired()])
#     introduction = TextAreaField('introduction', validators=[Length(min=0, max=500)])
#     movie_length = StringField('movie length', validators=[DataRequired()])
#     premiere_date = DateField('premiere date', validators=[DataRequired()])
#     special_effect = StringField('special effect', validators=[DataRequired()])
#     comments = TextAreaField('comments', validators=[Length(min=0, max=120)])
#     country = StringField('country', validators=[DataRequired()])
#     actors = StringField('actors', validators=[DataRequired()])
#     director = StringField('director', validators=[DataRequired()])
#
# class MovieDataForm(Form):
#     date = DateField('date', validators=[DataRequired()])
#     scene = IntegerField('scene', validators=[DataRequired()])
#     start_time = TimeField('start time', validators=[DataRequired()])
#     finish_time = TimeField('finish time', validators=[DataRequired()])
#     projection_hall = IntegerField('projection hall', validators=[DataRequired()])
#     price = IntegerField('price', validators=[DataRequired()])
#     score = IntegerField('score', validators=[DataRequired()])


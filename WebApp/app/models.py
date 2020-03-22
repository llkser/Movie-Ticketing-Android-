from flask_login._compat import unicode
from app import db
from flask_login import UserMixin, login_user, AnonymousUserMixin
from . import login_manager
from flask import request

Relationship = db.Table('relationship', db.Model.metadata,
                        db.Column('movie_id', db.Integer, db.ForeignKey('movie.movie_id')),
                        db.Column('company_name', db.String(32), db.ForeignKey('cinema.company_name'))
                        )

@login_manager.user_loader
def get_user(ident):
  return User.query.get(int(ident))

class User(UserMixin, db.Model):
    __tablename__ = 'user'
    user_id = db.Column(db.Integer, primary_key=True)
    user_name = db.Column(db.String(32), index=True)
    password = db.Column(db.String(32), index=True)
    mail = db.Column(db.String(32), index=True)
    gender = db.Column(db.String(8), index=True)
    avatar = db.Column(db.String(128), index=True)
    age = db.Column(db.Integer)
    phone_number = db.Column(db.Integer)
    user_vip_level = db.Column(db.Integer)
    orders = db.relationship('Order', backref='users', lazy='dynamic')

    def is_authenticated(self):
        return True

    def is_active(self):
        return True

    def is_anonymous(self):
        return False

    def get_id(self):
        return unicode(self.user_id)

    def __repr__(self):
        return '<User %r>' % self.user_id


class Movie(db.Model):
    __tablename__ = 'movie'
    movie_id = db.Column(db.Integer, primary_key=True)
    movie_name = db.Column(db.String(32), index=True)
    movie_type = db.Column(db.String(64), index=True)
    movie_length = db.Column(db.String(32), index=True)
    introduction = db.Column(db.String(512), index=True)
    country = db.Column(db.String(32), index=True)
    special_effect = db.Column(db.String(8), index=True)
    comment = db.Column(db.String(128), index=True)
    actors = db.Column(db.String(128), index=True)
    director = db.Column(db.String(32), index=True)
    poster = db.Column(db.String(128), index=True)
    premiere_date = db.Column(db.Date)
    date = db.Column(db.Date)
    start_time = db.Column(db.Time)
    finish_time = db.Column(db.Time)
    scene = db.Column(db.Integer)
    projection_hall = db.Column(db.Integer)
    price = db.Column(db.Integer)
    score = db.Column(db.Integer)
    orders = db.relationship('Order', backref='movies', lazy='dynamic')
    cinemas = db.relationship('Cinema', secondary=Relationship)


class Order(db.Model):
    __tablename__ = 'order'
    order_id = db.Column(db.Integer, primary_key=True)
    order_date = db.Column(db.DateTime, index=True)
    seat_number = db.Column(db.Integer)
    order_user = db.Column(db.Integer, db.ForeignKey('user.user_id'))
    order_movie = db.Column(db.Integer, db.ForeignKey('movie.movie_id'))


class Cinema(db.Model):
    __tablename__ = 'cinema'
    company_name = db.Column(db.String(32), primary_key=True)
    location = db.Column(db.String(32), index=True)
    phone_number = db.Column(db.String(16), index=True)
    service = db.Column(db.String(128), index=True)
    movies = db.relationship('Movie', secondary=Relationship)

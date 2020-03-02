from app import db

class goods(db.Model):
    id = db.Column(db.Integer, primary_key=True,autoincrement = True)
    name= db.Column(db.String(500), index=True)
    url=db.Column(db.String(500), index=True)
    saled = db.Column(db.Boolean, default=False, index=False)
    description=db.Column(db.String(500))
    User_id = db.Column(db.Integer, db.ForeignKey('user.id'))
    price=db.Column(db.Integer, default=1000)
    classify=db.Column(db.String(30))
    
'''
    def __repr__(self):
            return '' % (self.id, self.task, self.start_date, self.deadliney,self.deadlinem,self.deadlined,self.User_id)
'''
class User(db.Model):
    __tablename__ = 'user'
    id = db.Column(db.Integer, primary_key= True,autoincrement = True)
    name = db.Column(db.String(500), index=True,unique=True)
    gender = db.Column(db.String(20))
    email = db.Column(db.String(500), index=True,unique=True )
    password = db.Column(db.String(20))
    trader = db.Column(db.Boolean, default=False)
    good = db.relationship('goods', backref='User', lazy='dynamic')
    
'''
    def __repr__(self):
            return '' % (self.id, self.name, self.gender,self.email)
'''
class record(db.Model):
    id = db.Column(db.Integer, primary_key=True,autoincrement = True)
    goodid=db.Column(db.Integer)
    traderid = db.Column(db.Integer)
    userid= db.Column(db.Integer)
    
class log(db.Model):
    id = db.Column(db.Integer, primary_key=True,autoincrement = True)
    action=db.Column(db.String(80))
    goodname=db.Column(db.String(25))
    username= db.Column(db.String(25))
    host=db.Column(db.String(25))
    ip=db.Column(db.String(25))
    mac=db.Column(db.String(25))
    time=db.Column(db.DateTime)

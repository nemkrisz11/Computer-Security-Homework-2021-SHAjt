from flaskapp.database.db import db
from argon2 import PasswordHasher
from argon2.exceptions import VerifyMismatchError, VerificationError, InvalidHash
import datetime


# Model of a stored user
class User(db.Document):
    name = db.StringField(required=True, unique=True, min_length=4)
    password = db.StringField(required=True, min_length=8)
    isAdmin = db.BooleanField(required=True, default=False)
    regDate = db.DateTimeField(required=True, default=datetime.datetime.utcnow)
    meta = {
        "collection": "user"
    }

    # Password's hash are stored
    def hash_password(self):
        self.validate()
        ph = PasswordHasher()
        self.password = ph.hash(self.password)

    # Password's hash will be compared for verification
    def check_password(self, password):
        try:
            ph = PasswordHasher()
            ph.verify(self.password, password)
            if ph.check_needs_rehash(self.password):
                self.password = ph.hash(password)
            return True
        except (VerifyMismatchError, VerificationError, InvalidHash):
            return False

    # In case of password's change the hash will be stored
    def change_password(self, password):
        self.password = password
        self.hash_password()
        self.save()


# Model of an animated CAFF file in the database
class CaffAnimationImage(db.EmbeddedDocument):
    duration = db.IntField(required=True)
    width = db.IntField(required=True)
    height = db.IntField(required=True)
    caption = db.StringField()
    tags = db.ListField(db.StringField())


# Model of a comment in the database
class Comment(db.EmbeddedDocument):
    username = db.StringField(required=True)
    comment = db.StringField(required=True)
    date = db.DateTimeField(required=True, default=datetime.datetime.utcnow)


# Model of a CAFF file in the database
class CaffFile(db.Document):
    caffName = db.StringField(required=True)
    caffAnimationImage = db.EmbeddedDocumentField(CaffAnimationImage, required=True)
    numOfCiffs = db.IntField(required=True)
    creator = db.StringField()
    creationDate = db.DateTimeField()
    comments = db.EmbeddedDocumentListField(Comment)
    uploaderName = db.StringField(required=True)
    uploadDate = db.DateTimeField(required=True, default=datetime.datetime.utcnow)
    meta = {
        "collection": "caff_file"
    }

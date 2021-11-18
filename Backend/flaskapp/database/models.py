from .db import db
from argon2 import PasswordHasher
from argon2.exceptions import VerifyMismatchError, VerificationError, InvalidHash



# models here, required collection name caff_file
class CaffFile(db.Document):
    creator = db.StringField()
    creation_date = db.DateTimeField()


#Collection name must be user
class User(db.Document):
    name = db.StringField(required=True, unique=True)
    password = db.StringField(required=True, min_length=6)

    def hash_password(self):
        ph = PasswordHasher()
        self.password = ph.hash(self.password)

    def check_password(self, password):
        try:
            ph = PasswordHasher()
            return ph.verify(self.password, password)
        except (VerifyMismatchError, VerificationError, InvalidHash):
            return False


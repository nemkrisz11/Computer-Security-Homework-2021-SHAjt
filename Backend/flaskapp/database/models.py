from flaskapp.database.db import db
from argon2 import PasswordHasher
from argon2.exceptions import VerifyMismatchError, VerificationError, InvalidHash


class CaffFile(db.Document):
    creator = db.StringField()
    creation_date = db.DateTimeField()
    meta = {
        "collection": "caff_file"
    }


class User(db.Document):
    name = db.StringField(required=True, unique=True)
    password = db.StringField(required=True, min_length=8)
    meta = {
        "collection": "user"
    }

    def hash_password(self):
        ph = PasswordHasher()
        self.password = ph.hash(self.password)

    def check_password(self, password):
        try:
            ph = PasswordHasher()
            return ph.verify(self.password, password)
        except (VerifyMismatchError, VerificationError, InvalidHash):
            return False

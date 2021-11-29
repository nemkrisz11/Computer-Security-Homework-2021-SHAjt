from flask import request, jsonify, make_response
from flaskapp.database.models import User
from flaskapp.authorization import jwt_redis_blocklist, TOKEN_EXPIRES
from flask_restful import Resource
from flask_jwt_extended import create_access_token, get_jwt, current_user
from flask_jwt_extended import jwt_required
from mongoengine import ValidationError, NotUniqueError, DoesNotExist
import re


def password_policy(password):
    """
    Verify the strength of 'password'
    Returns a dict indicating the wrong criteria
    A password is considered strong if:
        8 characters length or more
        1 digit or more
        1 symbol or more
        1 uppercase letter or more
        1 lowercase letter or more
    """

    # calculating the length
    length_error = len(password) < 8

    # searching for digits
    digit_error = re.search(r"\d", password) is None

    # searching for uppercase
    uppercase_error = re.search(r"[A-Z]", password) is None

    # searching for lowercase
    lowercase_error = re.search(r"[a-z]", password) is None

    # searching for symbols
    symbol_error = re.search(r"\W", password) is None

    # overall result
    password_ok = not (length_error or digit_error or uppercase_error or lowercase_error or symbol_error)

    if password_ok:
        return None
    elif length_error:
        return jsonify(errorId="120", errorMessage="password too short")
    else:
        return jsonify(errorId="114", errorMessage="password too weak")


class RegisterApi(Resource):
    def post(self):
        body = request.get_json()
        if body is None:
            return make_response(jsonify(errorId="101", errorMessage="username cannot be empty"), 400)

        name = body.get('username')
        if name is None:
            return make_response(jsonify(errorId="101", errorMessage="username cannot be empty"), 400)

        password = body.get('password')
        if password is None:
            return make_response(jsonify(errorId="111", errorMessage="password cannot be empty"), 400)

        password_errors = password_policy(password)
        if password_errors is not None:
            return make_response(password_errors, 400)

        try:
            user = User(name=name, password=password)
            user.hash_password()
            user.save()
        except ValidationError as e:
            return make_response(jsonify(errorId="120", errorMessage=e.to_dict()), 400)
        except NotUniqueError as e:
            return make_response(jsonify(errorId="104", errorMessage={"username": "username is already taken"}), 400)

        return make_response(jsonify(id=str(user.id)), 200)


class LoginApi(Resource):
    def post(self):
        body = request.get_json()
        if body is None:
            return make_response(jsonify(errorId="101", errorMessage="username cannot be empty"), 400)

        try:
            user = User.objects.get(name=body.get('username'))
        except DoesNotExist:
            return make_response(jsonify(errorId="120", errorMessage="invalid username or password"), 400)

        authorized = user.check_password(body.get('password'))
        if not authorized:
            return make_response(jsonify(errorId="120", errorMessage="invalid username or password"), 400)

        access_token = create_access_token(identity=user, expires_delta=TOKEN_EXPIRES)
        return make_response(jsonify(token=access_token, expire=TOKEN_EXPIRES.total_seconds()), 200)


class LogoutApi(Resource):
    @jwt_required()
    def post(self):
        jti = get_jwt()["jti"]
        jwt_redis_blocklist.set(jti, "", ex=TOKEN_EXPIRES)
        return make_response(jsonify(message="successful logout"), 200)


class PasswordChangeApi(Resource):
    @jwt_required()
    def post(self):
        body = request.get_json()
        if body is None:
            return make_response(jsonify(errorId="111", errorMessage="password cannot be empty"), 400)

        new_password = body.get('password')
        if new_password is None:
            return make_response(jsonify(errorId="111", errorMessage="password cannot be empty"), 400)

        password_errors = password_policy(new_password)
        if password_errors is not None:
            return make_response(password_errors, 400)

        if current_user.isAdmin:
            try:
                if 'username' in body:
                    target_user = User.objects.get(name=body.get('username'))
                    target_user.change_password(new_password)
                else:
                    current_user.change_password(new_password)
            except ValidationError as e:
                return make_response(jsonify(errorId="120", errorMessage=e.to_dict()), 400)

            return make_response(jsonify(message="password change successful"), 200)

        else:
            if 'username' in body:
                return make_response(jsonify(errorId="002", errorMessage="forbidden interaction"), 403)
            else:
                try:
                    current_user.change_password(new_password)
                except ValidationError as e:
                    return make_response(jsonify(errorId="120", errorMessage=e.to_dict()), 400)

                return make_response(jsonify(message="password change successful"), 200)

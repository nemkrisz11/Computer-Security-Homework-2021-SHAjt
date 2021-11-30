from flask import request, jsonify, make_response
from flaskapp.database.models import User
from flask_restful import Resource
from flask_jwt_extended import jwt_required, current_user
from mongoengine import DoesNotExist
from math import ceil
from datetime import datetime
from flask import current_app
import logging

class UsersListApi(Resource):
    # Fetching the list of users
    @jwt_required()
    def get(self):
        page = request.args.get('page', 1, type=int)
        perpage = request.args.get('perpage', 20, type=int)

        if page is None or page < 1:
            return make_response(jsonify(errorId="003", errorMessage='incorrect arguments given'), 400)
        elif perpage is None or perpage < 1:
            return make_response(jsonify(errorId="004", errorMessage='incorrect arguments given'), 400)

        users = User.objects.skip((page - 1) * perpage).limit(perpage)

        return make_response(
            jsonify(
                users=[{"username": u.name,
                        "isAdmin": u.isAdmin,
                        "regDate": int(datetime.timestamp(u.regDate)*1000)} for u in users],
                totalPages=ceil(User.objects.count() / perpage)
            ), 200)


class UserDataApi(Resource):
    # Getting detailed user data for single user based on username
    @jwt_required()
    def get(self, username):
        try:
            user = User.objects.get(name=username)
            return make_response(
                jsonify(
                    username=user.name,
                    isAdmin=user.isAdmin,
                    regDate=int(datetime.timestamp(user.regDate)*1000)
                ), 200)

        except DoesNotExist:
            return make_response(jsonify(errorId="199", errorMessage='user not found'), 404)

    # Admin can remove user from database based on username
    @jwt_required()
    def delete(self, username):
        if current_user.isAdmin:
            user = User.objects.get(name=username)
            user.delete()
            current_app.logger.setLevel(logging.INFO)
            current_app.logger.info('User was deleted with the following username: ' + str(username))
            return make_response(jsonify(message='user delete successful'), 200)
        else:
            return make_response(jsonify(errorId="002", errorMessage='forbidden interaction'), 403)

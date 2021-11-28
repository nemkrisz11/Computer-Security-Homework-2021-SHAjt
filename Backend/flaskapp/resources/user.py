from flask import request, jsonify, make_response
from flaskapp.database.models import User
from flask_restful import Resource
from flask_jwt_extended import jwt_required, current_user
from math import ceil


class UsersListApi(Resource):
    @jwt_required()
    def get(self):
        page = int(request.args.get('page'))
        perpage = int(request.args.get('perpage'))

        if page is None or perpage is None:
            return make_response(jsonify(errorMessage='incorrect arguments given'), 400)

        users = User.objects.skip((page - 1) * perpage).limit(perpage)

        return make_response(jsonify(
            users=[{"username": u.name, "isAdmin": u.isAdmin, "regDate": u.regDate} for u in users],
            totalPages=ceil(User.objects.count() / perpage)
        ), 200)


class UserDataApi(Resource):
    @jwt_required()
    def get(self, username):
        user = User.objects.get(name=username)

        if user is None:
            return make_response(jsonify(errorMessage='user not found'), 404)

        return make_response(jsonify(username=user.name, idAdmin=user.isAdmin, regDate=user.regDate), 200)

    @jwt_required()
    def delete(self, username):
        if current_user.isAdmin:
            user = User.objects.get(name=username)
            user.delete()
            return make_response(jsonify(message='user delete successful'), 200)
        else:
            return make_response(jsonify(errorMessage='forbidden interaction'), 403)

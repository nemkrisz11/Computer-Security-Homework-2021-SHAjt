from flask import request, jsonify
from flaskapp.database.models import User
from flask_restful import Resource
from flask_jwt_extended import jwt_required
from math import ceil


class UsersListApi(Resource):
    @jwt_required()
    def get(self):
        page = int(request.args.get('page'))
        perpage = int(request.args.get('perpage'))

        if page is None or perpage is None:
            return jsonify(msg='incorrect arguments given'), 400

        users = User.objects.skip((page - 1) * perpage).limit(perpage)

        return jsonify(
            users=[{"username": u.name, "isAdmin": u.isAdmin, "regDate": u.regDate} for u in users],
            totalPages=ceil(User.objects.count() / perpage)
        ), 200

class UserDataApi(Resource):
    @jwt_required()
    def get(self):
        pass
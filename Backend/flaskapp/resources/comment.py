from flask import request, jsonify
from flaskapp.database.models import User
from flask_restful import Resource
from flask_jwt_extended import jwt_required


class CommentApi(Resource):
    def get(self):
        pass

    def post(self):
        pass


class DeleteCommentApi(Resource):
    def delete(self, comment_id):
        pass

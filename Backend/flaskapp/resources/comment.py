from flask import request, jsonify, make_response
from flaskapp.database.models import User
from flask_restful import Resource
from flask_jwt_extended import jwt_required, current_user
from flaskapp.database.models import CaffFile, Comment

class CommentApi(Resource):
    @jwt_required()
    def get(self):
        pass

    @jwt_required()
    def post(self):
        body = request.get_json()
        try:
            caff_id = body.get('caffId')
            comment = body.get('comment')
        except AttributeError:
            return make_response(jsonify(errorId="300", errorMessage="comment cannot be empty"), 400)
        if not comment:
            return make_response(jsonify(errorId="300", errorMessage="comment cannot be empty"), 400)
        if not caff_id:
            return make_response(jsonify(errorId="300", errorMessage="comment cannot be empty"), 400)

        if len(comment) > 200:
            return make_response(jsonify(errorId="301 ", errorMessage="comment too long"), 400)

        stored_file = CaffFile.objects.get(id=caff_id)
        new_comment = Comment(
            username=current_user.name,
            comment=comment
        )

        stored_file.comments.append(new_comment)
        stored_file.save()
        return make_response(jsonify(message='comment created successful'), 200)


    @jwt_required()
    def delete(self):
        # Params: caff_id, comment_id
        pass
from flask import request, jsonify, make_response
from flaskapp.database.models import User, CaffFile, CaffAnimationImage
from flask_restful import Resource
from flask_jwt_extended import jwt_required
from flask_jwt_extended import get_current_user
from caffparser import CiffFile, CiffFileHeader, CaffCredits, CaffHeader, CaffParser
from datetime import datetime
import os

ALLOWED_EXTENSIONS = {'caff'}

def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

class CaffDataApi(Resource):
    @jwt_required()
    def get(self, caff_id):
        pass

    @jwt_required()
    def delete(self, caff_id):
        pass


class CaffSearchApi(Resource):
    @jwt_required()
    def get(self):
        pass


class CaffUploadApi(Resource):
    @jwt_required()
    def post(self):
        if 'file' not in request.files:
            return make_response(jsonify(errorMessage="File not in request"), 400)

        try:
            name = request.form.get('name')
            file = request.files['file']

            if file.filename == '':
                return make_response(jsonify(errorMessage="No file selected for upload"), 400)
            if not allowed_file(file.filename):
                return make_response(jsonify(errorMessage="Invalid file format"), 400)

            uploader = get_current_user()

            caffParser = CaffParser()
            bytes = file.stream.read()
            parsed_file = caffParser.parse([b for b in bytes])

            previewAnimationImage = parsed_file.animationImages[0]
            caffFile = CaffFile(
                caffName=name,
                numOfCiffs=parsed_file.header.numOfCiffs,
                creator=parsed_file.credits.creator,
                creationDate=datetime(
                    parsed_file.credits.year,
                    parsed_file.credits.month,
                    parsed_file.credits.day,
                    parsed_file.credits.hour,
                    parsed_file.credits.minute
                ),
                comments=[],
                uploaderName=uploader.name,
                caffAnimationImage=CaffAnimationImage(
                    duration=previewAnimationImage.duration,
                    width=previewAnimationImage.ciffImage.header.width,
                    height=previewAnimationImage.ciffImage.header.height,
                    caption=previewAnimationImage.ciffImage.header.caption,
                    tags=previewAnimationImage.ciffImage.header.tags
                )
            )

            caffFile.save()

            filename = str(caffFile.id)
            with open(os.path.join(os.environ.get('UPLOAD_FOLDER', filename), 'wb+')) as f:
                f.write(bytes)
            # for local testing
            # with open(os.path.join('./uploads/', filename), 'wb+') as f:
            #     f.write(bytes)

            return make_response('', 201)
        except AttributeError:
            return make_response(jsonify(errorMessage="invalid parameters"), 400)



class CaffDownloadApi(Resource):
    @jwt_required()
    def get(self, caff_id):
        pass

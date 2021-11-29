from flask import request, jsonify, make_response, send_file
from mongoengine import DoesNotExist, ValidationError, Q

from flaskapp.database.models import User, CaffFile, CaffAnimationImage
from flask_restful import Resource
from flask_jwt_extended import jwt_required, current_user
from flask_jwt_extended import get_current_user
from caffparser import CiffFile, CiffFileHeader, CaffCredits, CaffHeader, CaffParser
from datetime import datetime
import os
from datetime import datetime, timedelta
import numpy as np
from math import ceil


ALLOWED_EXTENSIONS = {'caff'}

def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

def createPreviewCaffFile(file: CaffFile):
    caffParser = CaffParser()

    with open(os.path.join(os.environ.get('UPLOAD_FOLDER'), str(file.id)), "rb") as f:
    #with open(os.path.join('./uploads/', str(file.id)), "rb") as f:
        numpy_data = np.fromfile(f, np.dtype('B'))

    parsed_file = caffParser.parse(numpy_data)

    width = file.caffAnimationImage.width
    height = file.caffAnimationImage.height
    pixel_array = np.array(parsed_file.animationImages[0].ciffImage.pixelValues, dtype=np.uint8)
    pixel_array.resize(height, width, 3)

    preview_file = {
        "id": str(file.id),
        "numOfCiffs": file.numOfCiffs,
        "creationDate": int(datetime.timestamp(file.creationDate) * 1000),
        "creator": file.creator,
        "uploadDate": int(datetime.timestamp(file.uploadDate) * 1000),
        "uploaderName": file.uploaderName,
        "caffName": file.caffName,
        "caffAnimationImage": {
            "duration": file.caffAnimationImage.duration,
            "width": file.caffAnimationImage.width,
            "height": file.caffAnimationImage.height,
            "caption": file.caffAnimationImage.caption,
            "tags": file.caffAnimationImage.tags
        },
        "pixelValues": pixel_array.tolist()
    }

    return preview_file


def createPreviewCaffFileList(files):
    file_list = []
    for file in files:
        file_list.append(createPreviewCaffFile(file))

    return file_list


class CaffDataApi(Resource):
    @jwt_required()
    def get(self, caff_id):
        try:
            storedFile = CaffFile.objects.get(id=caff_id)
        except DoesNotExist:
            return make_response(jsonify(errorId="299", errorMessage="File does not exist"), 404)
        except ValidationError:
            return make_response(jsonify(errorId="299", errorMessage="File does not exist"), 404)

        preview_file = createPreviewCaffFile(storedFile)
        return make_response(jsonify(preview_file), 200)


    @jwt_required()
    def delete(self, caff_id):
        if current_user.isAdmin:
            try:
                storedFile = CaffFile.objects.get(id=caff_id)
            except DoesNotExist:
                return make_response(jsonify(errorId="299", errorMessage="File does not exist"), 404)
            except ValidationError:
                return make_response(jsonify(errorId="299", errorMessage="File does not exist"), 404)

            filepath = os.path.join(os.environ.get('UPLOAD_FOLDER'), str(storedFile.id))
            # filepath = os.path.join('./uploads/', str(storedFile.id))
            if os.path.exists(filepath):
                os.remove(filepath)
            storedFile.delete()
            return make_response(jsonify(message='CaffFile delete successful'), 200)
        else:
            return make_response(jsonify(errorId="002", errorMessage='forbidden interaction'), 403)


class CaffSearchApi(Resource):
    @jwt_required()
    def get(self):
        page = request.args.get('page', 1, type=int)
        perpage = request.args.get('perpage', 20, type=int)
        searchterm = request.args.get('searchTerm', "", type=str)
        creator = request.args.get('username', "", type=str)
        uploadername = request.args.get('uploaderName', "", type=str)
        creationdate = request.args.get('creationDate', -1, type=int)
        uploaddate = request.args.get('uploadDate', -1, type=int)

        if page < 1:
            return make_response(jsonify(errorId="003", errorMessage="Invalid page number"), 400)
        if perpage < 1:
            return make_response(jsonify(errorId="004", errorMessage="Invalid per page number"), 400)

        query = None
        if searchterm:
            if query:
                query &= Q(caffName__contains=searchterm)
            else:
                query = Q(name__contains=searchterm)
        if creator:
            if query:
                query &= Q(creator__contains=creator)
            else:
                query = Q(creator__contains=creator)
        if uploadername:
            if query:
                query &= Q(uploaderName__contains=uploadername)
            else:
                query = Q(uploaderName__contains=uploadername)
        if creationdate != -1:
            start = datetime.fromtimestamp(creationdate/1000)
            end = start + timedelta(days=1)
            if query:
                query &= Q(creationDate__gte=start) & Q(creationDate__lt=end)
            else:
                query = Q(creationDate__gte=start) & Q(creationDate__lt=end)
        if uploaddate != -1:
            start = datetime.fromtimestamp(uploaddate/1000)
            end = start + timedelta(days=1)
            if query:
                query &= Q(uploadDate__gte=start) & Q(uploadDate__lt=end)
            else:
                query = Q(uploadDate__gte=start) & Q(uploadDate__lt=end)

        caff_files = None
        if query:
            caff_files = CaffFile.objects(query)
        else:
            caff_files = CaffFile.objects()
        caff_files_total_count = len(caff_files)

        try:
            files = caff_files.paginate(page=page, per_page=perpage)
        except:
            return make_response(jsonify(errorId="003", errorMessage="Invalid page number"), 400)

        if len(files.items) != 0:
            preview_file_list = createPreviewCaffFileList(files.items)
        else:
            preview_file_list = []

        return make_response(jsonify(caffs=preview_file_list, totalPages=ceil(caff_files_total_count / perpage)), 200)


class CaffUploadApi(Resource):
    @jwt_required()
    def post(self):
        if 'file' not in request.files:
            return make_response(jsonify(errorId="299", errorMessage="file not in request"), 400)

        name = request.form.get('name')
        file = request.files['file']
        if name is None or file is None:
            return make_response(jsonify(errorId="299", errorMessage="invalid parameters"), 400)

        if file.filename == '':
            return make_response(jsonify(errorId="299", errorMessage="no file selected for upload"), 400)
        if not allowed_file(file.filename):
            return make_response(jsonify(errorId="200", errorMessage="invalid file format"), 400)

        uploader = get_current_user()

        caffParser = CaffParser()
        bytes = file.stream.read()

        try:
            parsed_file = caffParser.parse([b for b in bytes])
        except ValueError:
            return make_response(jsonify(errorId="200", errorMessage="invalid file format"), 400)

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

        # for docker
        with open(os.path.join(os.environ.get('UPLOAD_FOLDER'), filename), 'wb+') as f:
            f.write(bytes)

        # for local testing
        # with open(os.path.join('./uploads/', filename), 'wb+') as f:
        #     f.write(bytes)

        return make_response('', 201)


class CaffDownloadApi(Resource):
    @jwt_required()
    def get(self, caff_id):
        try:
            storedFile = CaffFile.objects.get(id=caff_id)
        except DoesNotExist:
            return make_response(jsonify(errorId="299", errorMessage="File does not exist"), 404)
        except ValidationError:
            return make_response(jsonify(errorId="299", errorMessage="File does not exist"), 404)

        filename = str(storedFile.id)
        filepath = os.path.join(os.environ.get('UPLOAD_FOLDER'), filename)
        #filepath = os.path.join('./uploads/', filename)
        if os.path.exists(filepath):
            return send_file(path_or_file=filepath, as_attachment=True, attachment_filename=storedFile.caffName)
        else:
            return make_response(jsonify(errorId="299", errorMessage="File does not exist"), 404)

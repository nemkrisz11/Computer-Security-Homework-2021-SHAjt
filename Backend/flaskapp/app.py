import os
from flask import Flask
from flask_restful import Api
from flaskapp.database.db import initialize_db
from flaskapp.resources.routes import initialize_routes
from flaskapp.authorization import jwt, TOKEN_EXPIRES
from flaskapp.database.models import User, CaffFile
# from flask_cors import CORS
import logging
from flask_limiter import Limiter
from flask_limiter.util import get_remote_address

#Init Flask application
#Configure database and JWT token properties
#Configure routes
def create_app():
    application = Flask(__name__)
    api = Api(application)
    limiter = Limiter(
        application,
        key_func=get_remote_address)


    logging.basicConfig(filename='/tmp/debug.log', level=logging.DEBUG)

    # For docker
    application.config['MONGODB_DB'] = os.environ['MONGODB_DATABASE']
    application.config['MONGODB_HOST'] = os.environ['MONGODB_HOSTNAME']
    application.config['MONGODB_PORT'] = int(os.environ['MONGODB_PORT'])
    application.config['MONGODB_USERNAME'] = os.environ['MONGODB_USERNAME']
    application.config['MONGODB_PASSWORD'] = os.environ['MONGODB_PASSWORD']

    # for local testing
    # application.config['MONGODB_SETTINGS'] = {
    #     'host': 'mongodb://localhost:27017/CaffDatabase'
    # }

    application.config['JWT_SECRET_KEY'] = "nMZSrZeLKofMnfJ7csa2zXwdvXWEqV7CBM327b2EVBGLwbioFYXjf3DAeZhdm7YL"
    application.config['JWT_ACCESS_TOKEN_EXPIRES'] = TOKEN_EXPIRES

    application.config['PROPAGATE_EXCEPTIONS'] = True

    # max file size is 50MB
    application.config['MAX_CONTENT_LENGTH'] = 50 * 1000 * 1000

    initialize_db(application)
    initialize_routes(api,limiter)
    jwt.init_app(application)
    # cors = CORS(application, resources={r"/*": {"origins": "*"}})

    # Initialize collections
    for cl in [User, CaffFile]:
        cl.ensure_indexes()

    return application


if __name__ == "__main__":
    application = create_app()
    ENV_DEBUG = os.environ.get("APP_DEBUG", True)
    ENV_PORT = os.environ.get("APP_PORT", 5000)

    application.run(host='0.0.0.0', port=ENV_PORT, debug=ENV_DEBUG)

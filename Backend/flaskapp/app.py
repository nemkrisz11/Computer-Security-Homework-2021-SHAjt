import os
from flask import Flask
from flask_restful import Api
from flaskapp.database.db import initialize_db
from flaskapp.resources.routes import initialize_routes
from flask_jwt_extended import JWTManager
# from flask_cors import CORS
import logging

application = Flask(__name__)
api = Api(application)

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

application.config['SECRET_KEY'] = "nMZSrZeLKofMnfJ7csa2zXwdvXWEqV7CBM327b2EVBGLwbioFYXjf3DAeZhdm7YL"

# cors = CORS(application, resources={r"/api/*": {"origins": "*"}})
initialize_db(application)
initialize_routes(api)
jwt = JWTManager(application)


if __name__ == "__main__":
    ENV_DEBUG = os.environ.get("APP_DEBUG", True)
    ENV_PORT = os.environ.get("APP_PORT", 5000)

    application.run(host='0.0.0.0', port=ENV_PORT, debug=ENV_DEBUG)

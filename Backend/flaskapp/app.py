import os
from datetime import datetime
from flask import Flask, request
from flask_jwt_extended import current_user
from flask_restful import Api
from flaskapp.database.db import initialize_db
from flaskapp.resources.routes import initialize_routes
from flaskapp.authorization import jwt, TOKEN_EXPIRES
from flaskapp.database.models import User, CaffFile
import logging
from logging import FileHandler
from flask_limiter import Limiter
from flask_limiter.util import get_remote_address
from werkzeug.middleware.proxy_fix import ProxyFix


# Init Flask application
# Configure database and JWT token properties
def create_app(limiter_enabled):
    application = Flask(__name__)
    # csrf = CSRFProtect()
    # csrf.init_app(application)
    api = Api(application)
    application.wsgi_app = ProxyFix(application.wsgi_app, x_for=1)
    limiter = Limiter(
        application,
        key_func=get_remote_address)
    limiter.enabled = limiter_enabled

    if application.logger.handlers:
        application.logger.handlers = []

    log_format = '%(utcnow)s - %(levelname)s - user_id:%(user_id)s - ip:%(ip)s - %(method)s - %(url)s - %(filename)s:%(lineno)s - %(message)s'

    handler = FileHandler('/var/log/flask/info.log')
    handler.setLevel(logging.INFO)

    handler.addFilter(ContextualFilter())
    handler.setFormatter(logging.Formatter(log_format))

    application.logger.addHandler(handler)
    handler_err = FileHandler('/var/log/flask/error.log')
    handler_err.setLevel(logging.ERROR)
    handler_err.addFilter(ContextualFilter())
    handler_err.setFormatter(logging.Formatter(log_format))
    application.logger.addHandler(handler_err)

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
    application.config['JWT_ACCESS_TOKEN_EXPIRES'] = TOKEN_EXPIRES

    application.config['PROPAGATE_EXCEPTIONS'] = True

    # max file size is 50MB
    application.config['MAX_CONTENT_LENGTH'] = 50 * 1000 * 1000

    initialize_db(application)
    initialize_routes(api, limiter)
    jwt.init_app(application)

    # Initialize collections
    for cl in [User, CaffFile]:
        cl.ensure_indexes()

    return application


class ContextualFilter(logging.Filter):
    def filter(self, log_record):
        log_record.utcnow = datetime.utcnow().strftime('%Y-%m-%d %H:%M:%S,%f %Z')
        log_record.url = request.path
        log_record.method = request.method
        log_record.ip = request.environ.get('HTTP_X_REAL_IP', request.remote_addr)
        if request.authorization is None or current_user.is_anonymous():
            log_record.user_id = 'guest'
        else:
            log_record.user_id = current_user.get_id()
        return True


if __name__ == "__main__":
    application = create_app(limiter_enabled=True)
    ENV_DEBUG = os.environ.get("APP_DEBUG", True)
    ENV_PORT = os.environ.get("APP_PORT", 5000)

    application.run(host='0.0.0.0', port=ENV_PORT, debug=ENV_DEBUG)

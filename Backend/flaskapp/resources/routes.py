from .auth import RegisterApi, LoginApi

# Api endpoints
def initialize_routes(api):
    api.add_resource(RegisterApi, '/api/register')
    api.add_resource(LoginApi, '/api/login')



from app import create_app

application = create_app(limiter_enabled=True)

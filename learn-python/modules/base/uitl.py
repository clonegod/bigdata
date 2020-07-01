from datetime import datetime as dt


def show_date():
    now = dt.now()
    print(now.strftime("%Y-%m-%d"))

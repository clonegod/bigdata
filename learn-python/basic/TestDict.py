
if __name__ == "__main__":
    d1 = {
        "status": 1
    }
    d2 = {
        "status": 2
    }
    d1 = dict(d1, **d2)
    print(d1)

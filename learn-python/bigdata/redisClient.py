import redis as redis
from datetime import datetime as dt
import threading
import time

"""
redis 取出的结果默认是字节，我们可以设定 decode_responses=True 改成字符串。
"""


class RedisModel(object):
    HOST = '127.0.0.1'
    PORT = 6379
    DB_NO = 0

    def __init__(self):
        if not hasattr(RedisModel, 'pool'):
            RedisModel.create_pool()
        self._connection = redis.Redis(connection_pool=RedisModel.pool)

    @staticmethod
    def create_pool():
        RedisModel.pool = redis.ConnectionPool(
            host=RedisModel.HOST,
            port=RedisModel.PORT,
            db=RedisModel.DB_NO,
            decode_responses=True)

    @property
    def connection(self):
        # id() 函数返回对象的唯一标识符，标识符是一个整数。
        print('poll_id={},conn={}'.format(id(RedisModel.pool), self._connection))
        return self._connection


##########################################################################################
def get_redis_conn(host="127.0.0.1", port=6379, db=0):
    return redis.Redis(host=host,
                       port=port,
                       db=db,
                       encoding="utf-8",
                       decode_responses=True)


def get_redis_conn_from_pool(host="127.0.0.1", port=6379, db=0):
    # TODO：将pool独立出来使用
    pool = redis.ConnectionPool(host=host, port=port, db=db, decode_responses=True)
    return redis.Redis(connection_pool=pool)


def test_create_conn_per_request():
    conn = get_redis_conn()
    conn.set('name', 'alice-' + str(int(dt.now().timestamp())))
    rs = conn.get('name')
    print(rs)
    conn.close()


def test_create_conn_from_poll():
    conn = get_redis_conn_from_pool()
    conn.set('country', '中国-' + str(int(dt.now().timestamp())))
    rs = conn.get('country')
    print(rs)
    conn.close()


def test_redis_pool_singleton(n):
    conn = RedisModel().connection
    conn.set("age", 20 + n)
    print(conn.get("age"))
    conn.close()


if __name__ == '__main__':
    test_create_conn_per_request()
    test_create_conn_from_poll()

    for i in range(10):
        t = threading.Thread(target=test_redis_pool_singleton, args=[i, ])
        t.start()

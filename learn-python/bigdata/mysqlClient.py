import pandas as pd

from sqlalchemy import create_engine


def create_mysql_engine():
    conn_str_template = "mysql+pymysql://{user}:{password}@{host}:{port}/{db}?charset=utf8"
    conn_str = conn_str_template.format(user="root",
                                        password="123456",
                                        host="localhost",
                                        port=3306,
                                        db="test"
                                        )
    return create_engine(conn_str)


def test_read(conn, sql):
    rs = pd.read_sql_query(sql, conn)
    for index, row in rs.iterrows():
        str_fmt = "index={index}, id={id}, name={name}, create_time={create_time}, update_time={update_time}"
        print(str_fmt.format(index=index, **row))


def test_write(conn, sql):
    conn.execute(sql)


if __name__ == '__main__':
    db = create_mysql_engine()
    conn = db.connect()
    test_write(conn, "insert into t_person (name) values ('zooe');")
    test_read(conn, "select * from t_person;")
    test_write(conn, "delete from t_person where name = 'zooe';")
    conn.close()
    db.dispose()

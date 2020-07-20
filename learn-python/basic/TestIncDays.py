#!/usr/bin/python
# -*- coding: utf-8 -*-

from datetime import datetime, timedelta
import requests
import time

BIZ_TYPES = ["applyInfo", "singleLoanAccountInfo", "singleLoanRepayInfo"]


def execute(start_date_str, days, modify_version=""):
    start_date = datetime.strptime(start_date_str, '%Y-%m-%d')
    for x in range(days):
        next_date = start_date + timedelta(x)
        next_next_date = start_date + timedelta(x + 1)
        begin_date = next_date.strftime('%Y-%m-%d')
        end_date = next_next_date.strftime('%Y-%m-%d')
        print("start process date: %s" % begin_date)
        time.sleep(3)
        for biz_type in BIZ_TYPES:
            url = 'http://localhost:8080/api/bh/upload/date/{biz_type}/{begin_date}/{end_date}?batch=1&reserved=true&modifyVersion={modify_version}'.format(
                biz_type=biz_type, begin_date=begin_date, end_date=end_date, modify_version=modify_version)
            print(url)
            res = requests.get(url)
            print(res.text)
    print("finish!")


if __name__ == '__main__':
    execute('2019-12-12', 7, 'M')

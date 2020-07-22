#!/usr/bin/python
# -*- coding: utf-8 -*-
from datetime import datetime, timedelta
import requests
import time
import threading

BIZ_TYPES = ["applyInfo", "singleLoanAccountInfo", "singleLoanRepayInfo"]


def execute(biz_type, start_date_str, days, modify_version=""):
    start_date = datetime.strptime(start_date_str, '%Y-%m-%d')
    for x in range(days):
        next_date = start_date + timedelta(x)
        next_next_date = start_date + timedelta(x + 1)
        begin_date = next_date.strftime('%Y-%m-%d')
        end_date = next_next_date.strftime('%Y-%m-%d')
        url = 'http://localhost:8080/api/bh/upload/date/{biz_type}/{begin_date}/{end_date}?batch=1&reserved=true&modifyVersion={modify_version}'.format(
            biz_type=biz_type, begin_date=begin_date, end_date=end_date, modify_version=modify_version)
        print("\n%s start process %s: %s\n %s\n" % (threading.currentThread().name, biz_type, begin_date, url))
        res = requests.get(url)
        print(res.text)
    print("%s finish!" % biz_type)


if __name__ == '__main__':
    print('program start at: %s' % datetime.now().strftime('%Y-%m-%d %H:%M:%S'))
    stats_url = "http://localhost:8080/api/bh/upload/recordStats?reset=true"
    res = requests.get(stats_url)
    print(stats_url)
    print(res.text)

    for biz_type in BIZ_TYPES:
        threading.Thread(target=execute, args=(biz_type, '2019-12-12', 1, 'M1')).start()
        time.sleep(1)
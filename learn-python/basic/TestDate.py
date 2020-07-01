# -*- coding:utf-8 -*-

import datetime

if __name__ == "__main__":
    n = 0
    while True:
        now = datetime.datetime.now()
        # 计算n天前的日期
        newDay = now - datetime.timedelta(days=n)
        print(newDay.strftime("%Y-%m-%d"))
        n = n + 1
        if n > 100:
            break

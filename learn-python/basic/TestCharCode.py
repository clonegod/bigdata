#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import sys

lang = '中文'

# 中文的10进制
# 20013
print(chr(20013), ':', ord('中'))
# 25991
print(chr(25991), ':', ord('文'))

# 10进制转16进制
# 0x4e2d
print(hex(20013))
# 0x6587
print(hex(25991))

# unicode码
print('中文'.encode('unicode-escape'))
print('\u4e2d\u6587')


# byte 字节
# b'\xe4\xb8\xad\xe6\x96\x87'
print('中文'.encode('utf-8'))
print(b'\xe4\xb8\xad\xe6\x96\x87'.decode('utf-8'))


str = """**Google-campaign cplps**异常报警<font color=\"warning\">10条</font> \n
预警时间: <font color=\"comment\">2020-04-02 10:30</font>\n
详情:\n
> campaign_id: <font color=\"comment\">23843359015890748</font>
> campaign_name: <font color=\"comment\">Hamobi_Tunaikita_ID_And_06.25 - JX01</font>
> 当日cplps: <font color=\"warning\">13.3</font>
> 当日cost: <font color=\"warning\">500</font>
> 当日报警次数: 5次
\n
> campaign_id: <font color=\"comment\">23843474929300090</font>
> campaign_name: <font color=\"comment\">cyberads_tunaikita_ID_ZT_0701_5_3</font>
> 当日cplps: <font color=\"warning\">13.3</font>
> 当日cost: <font color=\"warning\">500</font>
> 当日报警次数: 5次"""

if __name__ == "__main__":
    print('sys.argv: ', len(sys.argv))
    print(len(str))

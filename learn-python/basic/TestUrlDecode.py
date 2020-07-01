import urllib.parse


def testEncodeObject():
    values = {}
    values['username'] = '02蔡彩虹'
    values['password'] = 'ddddd?'
    url = "http://www.baidu.com"
    data = urllib.parse.urlencode(values)
    print(data)


def testEncodeObjectString():
    s = '长春'
    s = urllib.parse.quote(s)
    print(s)


def testDecode():
    str = 'https://graph.facebook.com/v6.0/act_481594902424943/insights?time_range={%27since%27:%272020-03-19%27,%27until%27:%272020-03-19%27}&level=ad&time_increment=1&access_token=EAAgj0IhU4z8BAMfx4Otf5bL7bZCIpzOyLroIECX4TZCkS4N8qyPQRm5SnIR57bzLGSQEu16b28XQWJ9UjWmkZAA57wZC9JKpGQN20YWBCZAPiIl0fvTwpdKB6fn5lXYmC8QqajqIXSrQxwx6lsgEW2ZAndF2O3Ug07kNDyAFJyKgZDZD&fields=ad_id,ad_name,account_id,account_name,adset_id,adset_name,campaign_id,campaign_name,instant_experience_clicks_to_start,instant_experience_clicks_to_open,instant_experience_outbound_clicks'
    decode_str = urllib.parse.unquote(str)
    print(decode_str)


if __name__ == '__main__':
    testEncodeObject()
    testEncodeObjectString()
    testDecode()

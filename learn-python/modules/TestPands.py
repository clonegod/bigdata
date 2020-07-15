import numpy as np
import pandas as pd

"""
DataFrame - 对二维表中的数据进行处理
"""


# 创建
def test_create_data_frame():
    data = np.array([[1, 2, 3], [4, 5, 6], [7, 8, 9]])
    c = ['a', 'b', 'c']
    r = ['A', 'B', 'C']
    df = pd.DataFrame(data=data, columns=c, index=r)
    print(df)
    return df


if __name__ == '__main__':
    df = test_create_data_frame()

    # 按行名排序，降序
    print(df.sort_index(axis=0, ascending=False))

    # 按列名排序，降序
    print(df.sort_index(axis=1, ascending=False))

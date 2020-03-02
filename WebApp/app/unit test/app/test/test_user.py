# coding:utf-8
import unittest
from demo import app
import json
class TestLogin(unittest.TestCase):
    """定义测试案例"""
    # 测试代码执行之前调用 (方法名固定)
    def setUp(self):
        app.testing = True
        self.client = app.test_client()
    def test_empty_name_password(self):
        response = self.client.post("/dologin", data={})
        # respoonse.data是响应体数据
        resp_json = response.data
        # 按照json解析
        resp_json =resp_json.decode("utf-8")
        resp_dict = json.loads(resp_json)
        # 使用断言进行验证
        self.assertIn("code", resp_dict)
        code = resp_dict.get("code")
        self.assertEqual(code, 1)
        # 测试只传name
        response = self.client.post("/dologin", data={"name": "admin"})
        # respoonse.data是响应体数据
        resp_json = response.data
        # 按照json解析
        resp_json =resp_json.decode("utf-8")
        resp_dict = json.loads(resp_json)
        # 使用断言进行验证
        self.assertIn("code", resp_dict)
        code = resp_dict.get("code")
        self.assertEqual(code, 1)
        # 测试代码。 (方法名必须以"test_"开头)
    def test_wrong_name_password(self):
        """测试用户名或密码错误"""
        response = self.client.post("/dologin", data={"name": "admin", "password": "xxx"})
        # respoonse.data是响应体数据
        resp_json = response.data
        # 按照json解析
        resp_json =resp_json.decode("utf-8")
        resp_dict = json.loads(resp_json)
        # 使用断言进行验证
        self.assertIn("code", resp_dict)
        code = resp_dict.get("code")
        self.assertEqual(code, 2)
        
if __name__ == '__main__':
    unittest.main()

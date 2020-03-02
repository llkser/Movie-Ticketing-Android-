import unittest
from .views import app


import json

class TestLogin(unittest.TestCase):
    
    def setUp(self):
         app.config['TESTING'] = True

         self.client = app.test_client()
    def test_empty_name_password_email(self):
        response = self.client.post('/regist', data={"name":"","gender":"Male","trader":"T","password":"","email":""})
        resp_json = response.data
        a=resp_json.decode("utf-8")
        resp_dict = json.loads(a)
        self.assertIn("code", resp_dict)
        code = resp_dict.get("code")
        print(resp_dict.get("message"))
        self.assertEqual(code, 1)

        
    def test_empty(self):
        response = self.client.post('/regist', data={})
        resp_json = response.data
        a=resp_json.decode("utf-8")
        resp_dict = json.loads(a)
        self.assertIn("code", resp_dict)
        code = resp_dict.get("code")
        print(resp_dict.get("message"))
        self.assertEqual(code, 1)

    def test_repeat_name(self):
        response = self.client.post('/regist', data={"name":"g","gender":"Male","trader":"T","password":"123","email":"123"})
        resp_json = response.data
        a=resp_json.decode("utf-8")
        resp_dict = json.loads(a)
        self.assertIn("code", resp_dict)
        code = resp_dict.get("code")
        print(resp_dict.get("message"))
        self.assertEqual(code, 1)

    def test_repeat_email(self):
        response = self.client.post('/regist', data={"name":"ggg","gender":"Male","trader":"T","password":"123","email":"Nightmaremlp@163.com"})
        resp_json = response.data
        a=resp_json.decode("utf-8")
        resp_dict = json.loads(a)
        self.assertIn("code", resp_dict)
        code = resp_dict.get("code")
        print(resp_dict.get("message"))
        self.assertEqual(code, 1)

    def test_unregistered_name_email(self):
        response = self.client.post('/regist', data={"name":"ggg","gender":"Male","trader":"T","password":"123","email":"HHH@163.com"})
        resp_json = response.data
        a=resp_json.decode("utf-8")
        resp_dict = json.loads(a)
        self.assertIn("code", resp_dict)
        code = resp_dict.get("code")
        print(resp_dict.get("message"))
        self.assertEqual(code, 1)

    def test_unregistered_name_out_of_range(self):
        response = self.client.post('/regist', data={"name":"gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg","gender":"Male","trader":"T","password":"123","email":"HHH@163.com"})
        resp_json = response.data
        a=resp_json.decode("utf-8")
        resp_dict = json.loads(a)
        self.assertIn("code", resp_dict)
        code = resp_dict.get("code")
        print(resp_dict.get("message"))
        self.assertEqual(code, 1)

    def test_log_wrong_name(self):
        response = self.client.post('/login', data={"name":"ggg","password":"123","email":"HHH@163.com"})
        resp_json = response.data
        a=resp_json.decode("utf-8")
        resp_dict = json.loads(a)
        self.assertIn("code", resp_dict)
        code = resp_dict.get("code")
        print(resp_dict.get("message"))
        self.assertEqual(code, 1)

    def test_log_wrong_email(self):
        response = self.client.post('/login', data={"name":"g","password":"123","email":"HHH@163.com"})
        resp_json = response.data
        a=resp_json.decode("utf-8")
        resp_dict = json.loads(a)
        self.assertIn("code", resp_dict)
        code = resp_dict.get("code")
        print(resp_dict.get("message"))
        self.assertEqual(code, 1)

    def test_log_wrong_password(self):
        response = self.client.post('/login', data={"name":"g","password":"123","email":"Nightmaremlp@163.com"})
        resp_json = response.data
        a=resp_json.decode("utf-8")
        resp_dict = json.loads(a)
        self.assertIn("code", resp_dict)
        code = resp_dict.get("code")
        print(resp_dict.get("message"))
        self.assertEqual(code, 1)

    def test_log_correct_password(self):
        response = self.client.post('/login', data={"name":"g","password":"fsf","email":"Nightmaremlp@163.com"})
        resp_json = response.data
        a=resp_json.decode("utf-8")
        resp_dict = json.loads(a)
        self.assertIn("code", resp_dict)
        code = resp_dict.get("code")
        print(resp_dict.get("message"))
        self.assertEqual(code, 1)

    def test_log_name_out_of_range(self):
        response = self.client.post('/login', data={"name":"gggggggggggggggggggggggggggggggggggggggggggggggggggggggg","password":"fsf","email":"Nightmaremlp@163.com"})
        resp_json = response.data
        a=resp_json.decode("utf-8")
        resp_dict = json.loads(a)
        self.assertIn("code", resp_dict)
        code = resp_dict.get("code")
        print(resp_dict.get("message"))
        self.assertEqual(code, 1)

if __name__ == '__main__':
    unittest.main()  # 进行测试

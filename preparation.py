import random
import pymysql
import requests
from lxml import etree

headers = {
    'user-agent':
    'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.75 Safari/537.36 Edg/100.0.1185.36',
}

ans = [[]]
k = 374
db = pymysql.connect(host='localhost',
                     user='root',
                     password='200273shz',
                     database='shao_wm')
cur = db.cursor()
for i in range(2, 8):
    # params = {'start': i * 25}
    # /html/body/div[5]/div/div[1]/div[2]/ul/li[*]/div[2]/h2/a/@href
    # https://home.meishichina.com/recipe/recai
    # https://home.meishichina.com/recipe/recai/page/2
    # https://home.meishichina.com/recipe/recai/page/3
    # https://home.meishichina.com/recipe/recai/page/2
    print(i)
    url = f'https://home.meishichina.com/recipe/yinpin/page/{i}'
    res = requests.get(url=url, headers=headers)
    res.encoding = 'utf-8'
    html = etree.HTML(res.text)
    nurl = html.xpath(
        '/html/body/div[5]/div/div[1]/div[2]/ul/li[*]/div[2]/h2/a/@href')

    for x in nurl:
        res = requests.get(url=x, headers=headers)
        res.encoding = 'utf-8'
        # /html/body/div[5]/div/div[1]/div[2]/h1/a/text()
        html = etree.HTML(res.text)
        title = html.xpath('/html/body/div[5]/div/div[1]/div[2]/h1/a/text()')
        img = html.xpath(
            '/html/body/div[5]/div/div[1]/div[3]/div/div[1]/a/img/@src')
        # /html/body/div[5]/div/div[1]/div[3]/div/div[2]/h3
        # print(img)
        img_url = img[0]
        # with open(img, 'wb') as f:
        pic = requests.get(img_url, headers=headers)
        storelocation = 'G:/WV-static/imgs/美食' + str(k) + '.jpg'
        relative_url = '/img/美食' + str(k) + '.jpg'
        with open(storelocation, 'wb') as f:
            f.write(pic.content)
        k += 1
        mingxi = html.xpath(
            '/html/body/div[5]/div/div[1]/div[3]/div/div[2]/h3/text()')
        zhuliao = html.xpath(
            '/html/body/div[5]/div/div[1]/div[3]/div/fieldset[1]/legend/text()'
        )
        zhuliaocontent = html.xpath(
            ' /html/body/div[5]/div/div[1]/div[3]/div/fieldset[1]/div/ul/li[*]/span[1]/a/b/text()'
        )
        if not title:
            continue
        # print(zhuliaocontent)
        introdution = '好吃又美味的'
        introdution += title[0]
        introdution += '    '
        introdution += '主料为:'
        for y in zhuliaocontent:
            introdution += y
            y += ' '

        texingcontent = html.xpath(
            '/html/body/div[5]/div/div[1]/div[3]/div/div[3]/ul/li[*]/span[1]/a/text()'
        )
        introdution += '味道:'
        introdution += texingcontent[0]
        introdution += ' '
        introdution += '做菜方式:'
        introdution += texingcontent[1]
        introdution += ' '
        introdution += '做菜所需时间:'
        introdution += texingcontent[2]
        introdution += ' '
        # print(introdution)

        # 基本4个参数，其他参数ctrl+点击connect 查看其他参数

        sql = "insert into menu(title,price, cost, introduction, location,tid) values(%s,%s,%s,%s,%s,%s)"
        price = random.randint(45, 150)
        cost = price - random.randint(15, 35)
        value = (title[0], price, cost, introdution, relative_url, 7)
        cur.execute(sql, value)
        db.commit()

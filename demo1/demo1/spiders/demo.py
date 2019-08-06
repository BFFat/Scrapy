
import urllib

import scrapy
from demo1.items import Demo1Item

# BOSS直聘网站爬虫职位

class DemoSpider(scrapy.Spider):
    # 爬虫名， 启动爬虫时需要的参数*必填
    name = 'demo'
    # 爬取域范围，允许爬虫在这个域名下进行爬取（可选）
    # allowed_domains = ['www.zhipin.com']
    # 爬虫需要的url
    start_urls = ['https://www.zhipin.com/c101280600/?query=开发']

    # response会得到爬虫的url
    def parse(self, response):
        node_list = response.xpath("//div[@class='job-primary']")
        # 用来存储所有的item字段
        # items = []

        for node in node_list:
            item = Demo1Item()
            # extract() 将xpath对象转换为Unicode字符串
            item['company_name'] = node.xpath(".//div[@class='info-company']//a/text()").extract_first()
            item['working_place'] = node.xpath(".//div[@class='info-primary']/p/text()").extract_first()
            item['job_title'] = node.xpath(".//div[@class='info-primary']//a/div[@class='job-title']/text()").extract_first()
            item['salary'] = node.xpath(".//div[@class='info-primary']//a/span/text()").extract_first()
            item['href'] = node.xpath(".//div[@class='info-primary']//a/@href").extract_first()
            item['icon'] = node.xpath(".//div[@class='info-publis']//h3//img/@src").extract_first()

            # 将爬取的数据导出到'.txt'文本
            with open('text.txt', 'a', encoding = 'utf-8') as f:
                f.writelines(item['company_name'] + ' \n')
                f.writelines(item['working_place'] + ' \n')
                f.writelines(item['job_title'] + ' \n')
                f.writelines(item['salary'] + ' \n')
                f.writelines(item['icon'] + ' \n')
                f.writelines('https://www.zhipin.com' + item['href'] + ' \n' + ' \n')

            # 将icon路径截取，得到自己需要的部分
            iconpath = item['icon']
            trueIconpath = iconpath[:-40]
            newIconpath = trueIconpath[trueIconpath.rfind('/') + 1 :]

            # 下载图片到本地指定路径位置
            urllib.request.urlretrieve(node.xpath("//div[@class='info-publis']//h3/img/@src").extract_first(), 'G:\\爬虫\\' + newIconpath)

            # 返回提取到的每个item数据给管道处理，同时还会出来继续执行后面的代码
            yield item

        # 输出当前爬虫response得到的url
        print("before:" + response.url)

        # 制定下一页的url传回给scrapy请求
        # 当网页的下一页没有链接时（href=javascipt:;), 结束爬虫
        newurl = 'https://www.zhipin.com' + response.xpath(".//div[@id='main']//div[@class='job-list']//div[@class='page']//a[@class='next']/@href").extract_first()

        yield scrapy.Request(newurl)

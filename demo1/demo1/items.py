# -*- coding: utf-8 -*-

# Define here the models for your scraped items
#
# See documentation in:
# https://docs.scrapy.org/en/latest/topics/items.html

import scrapy


class Demo1Item(scrapy.Item):
    # define the fields for your item here like:
    # name = scrapy.Field()

    # 公司名字
    company_name = scrapy.Field()
    # 工作地点
    working_place = scrapy.Field()
    # 职位
    job_title = scrapy.Field()
    # 工资
    salary = scrapy.Field()
    # 图标
    icon = scrapy.Field()
    # 链接
    href = scrapy.Field()




# -*- coding: utf-8 -*-

# Define your item pipelines here
#
# Don't forget to add your pipeline to the ITEM_PIPELINES setting
# See: https://docs.scrapy.org/en/latest/topics/item-pipeline.html
import codecs
import json

class Demo1Pipeline(object):
    def __init__(self):
        self.file = codecs.open('demo1/data/demo.json', 'w')

    def process_item(self, item, spider):

        content = json.dumps(dict(item), ensure_ascii=False) + ",\n"
        self.file.write(content)
        return item


    def close_spider(self, spider):
        self.file.close()
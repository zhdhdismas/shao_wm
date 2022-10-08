#-*-coding:utf-8*-
import xlsxwriter

# create a new Excel file and add a worksheet
# 创建工作薄 workbook('demo.xlsx')
workbook = xlsxwriter.Workbook('demo.xlsx')
# 创建工作表
worksheet = workbook.add_worksheet()

# Widen the first column to make the text clearer
# 设置一列或者多列单元属性
worksheet.set_column('A:A',20)

# Add a bold format to use to highlight cells
# 在工作表中创建一个新的格式对象来格式化单元格，实现加粗
bold = workbook.add_format({'bold': True})

# write some simple text.
# 工总表写入简单文本

worksheet.write('A1', 'hello')

# Text with formatting
# 工作表写入带有格式的文本，加粗
worksheet.write('A2', 'World', bold)

# Write some numbers, with row/column notation #按照坐标写入

worksheet.write(2, 0, 123)
worksheet.write(3, 0, 123.456)

# Insert an image.
# 插入图片
worksheet.insert_image('B5', 'lena.png')
# 关闭工作薄
workbook.close()
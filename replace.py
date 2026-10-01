import re

def replace_calls(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # replace alert(msg) with await App.showModal('Thông báo', msg)
    # Be careful not to replace inner alerts if any, but regex should work for simple cases.
    content = re.sub(r"alert\((.*?)\)", r"await App.showModal('Thông báo', \1)", content)
    
    # replace confirm(msg) with await App.confirmModal('Xác nhận', msg)
    content = re.sub(r"confirm\((.*?)\)", r"await App.confirmModal('Xác nhận', \1)", content)

    # replace prompt(msg, default) with await App.promptModal('Nhập', msg, default)
    content = re.sub(r"prompt\((.*?),\s*(.*?)\)", r"await App.promptModal('Nhập', \1, \2)", content)

    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)

for p in ['src/main/resources/static/js/admin.js', 'src/main/resources/static/js/transfer.js']:
    replace_calls(p)

## xml is inbuild python library no dependency third party library
### 🐍🐍🐍 happy coding. 🐍🐍🐍
```
from xml.etree import ElementTree as ET
a='''
<numbers>
  <number>33</number>
  <number>34.4</number>
  <number>33.8</number>
  <number>33.43</number>
  <number>34.46</number>
  <number>35</number>
  <number>33.49</number>
  <number>33.00</number>
</numbers>
'''
tree = ET.fromstring(a)
arrynum = tree.findall(".//number")
print("arrynum",arrynum)
sum = 0 
for ele in arrynum:
    sum+= float(ele.text)
print(sum)
```

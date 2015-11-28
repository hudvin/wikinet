# wikinet
This tools allows to convert wikipedia xml dump to json format and extract outgoing links from pages(by means sweble parser).

##How to build
1. git clone https://github.com/hudvin/wikinet
2. Go to wikinet dir and run gradle distZip.  This will create distibution pack in spark_tools/build/distributions. 
3. Go to this folder and unpack dist via unzip spark_tools.zip

##Wikipedia dumps
You can download dump from here https://dumps.wikimedia.org/enwiki/latest/enwiki-latest-pages-articles.xml.bz2. 
Now it  contains somewhere around 15 000 000 pages in xml format.
To convert this dump to json format(one page per line) , run:

./spark_tools convert  enwiki-latest-pages-articles.xml.bz2 enwiki-latest-pages-articles.json.bz2

In order to extract outgoing links run this:
./spark_tools extract enwiki-latest-pages-articles.json.bz2  links_dir

You will have files with content like this:

"Anarchism"->"collectivist anarchism"

"Anarchism"->"anarcho-syndicalism"

"Anarchism"->"Mutualism (economic theory)"

"Anarchism"->"participatory economics"


Keep in mind, this takes at least 15 hours (on my pc).
Generally, sweble can extract much more information - titles, paragraphs, images and so on. But right now 
I don't need all this information for my purposes.

# parsedate.rb: Written by Tadayoshi Funaba 2001, 2002
# $Id: parsedate.rb,v 1.2 2006/06/10 19:13:58 headius Exp $

require 'date/format'

module ParseDate

  def parsedate(str, comp=false)
    Date._parse(str, comp).
      values_at(:year, :mon, :mday, :hour, :min, :sec, :zone, :wday)
  end

  module_function :parsedate

end

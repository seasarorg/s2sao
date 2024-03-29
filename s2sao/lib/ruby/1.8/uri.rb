#
# URI support for Ruby
#
# Author:: Akira Yamada <akira@ruby-lang.org>
# Documentation:: Akira Yamada <akira@ruby-lang.org>, Dmitry V. Sabanin <sdmitry@lrn.ru>
# License:: 
#  Copyright (c) 2001 akira yamada <akira@ruby-lang.org>
#  You can redistribute it and/or modify it under the same term as Ruby.
# Revision:: $Id: uri.rb,v 1.2 2006/06/10 19:14:00 headius Exp $
# 
# See URI for documentation
#

module URI
  # :stopdoc:
  VERSION_CODE = '000911'.freeze
  VERSION = VERSION_CODE.scan(/../).collect{|n| n.to_i}.join('.').freeze
  # :startdoc:

end

require 'uri/common'
require 'uri/generic'
require 'uri/ftp'
require 'uri/http'
require 'uri/https'
require 'uri/ldap'
require 'uri/mailto'

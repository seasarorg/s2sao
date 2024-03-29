# = ERB -- Ruby Templating
#
# Author:: Masatoshi SEKI
# Documentation:: James Edward Gray II and Gavin Sinclair
#
# See ERB for primary documentation and ERB::Util for a couple of utility
# routines.
#
# Copyright (c) 1999-2000,2002,2003 Masatoshi SEKI
#
# You can redistribute it and/or modify it under the same terms as Ruby.

#
# = ERB -- Ruby Templating
#
# == Introduction
#
# ERB provides an easy to use but powerful templating system for Ruby.  Using
# ERB, actual Ruby code can be added to any plain text document for the
# purposes of generating document information details and/or flow control.
#
# A very simple example is this:
# 
#   require 'erb'
#
#   x = 42
#   template = ERB.new <<-EOF
#     The value of x is: <%= x %>
#   EOF
#   puts template.result(binding)
#
# <em>Prints:</em> The value of x is: 42
#
# More complex examples are given below.
#
#
# == Recognized Tags
#
# ERB recognizes certain tags in the provided template and converts them based
# on the rules below:
#
#   <% Ruby code -- inline with output %>
#   <%= Ruby expression -- replace with result %>
#   <%# comment -- ignored -- useful in testing %>
#   % a line of Ruby code -- treated as <% line %> (optional -- see ERB.new)
#   %% replaced with % if first thing on a line and % processing is used
#   <%% or %%> -- replace with <% or %> respectively
#
# All other text is passed through ERB filtering unchanged.
#
#
# == Options
#
# There are several settings you can change when you use ERB:
# * the nature of the tags that are recognized;
# * the value of <tt>$SAFE</tt> under which the template is run;
# * the binding used to resolve local variables in the template.
#
# See the ERB.new and ERB#result methods for more detail.
#
#
# == Examples
#
# === Plain Text
#
# ERB is useful for any generic templating situation.  Note that in this example, we use the
# convenient "% at start of line" tag, and we quote the template literally with
# <tt>%q{...}</tt> to avoid trouble with the backslash.
#
#   require "erb"
#   
#   # Create template.
#   template = %q{
#     From:  James Edward Gray II <james@grayproductions.net>
#     To:  <%= to %>
#     Subject:  Addressing Needs
#   
#     <%= to[/\w+/] %>:
#   
#     Just wanted to send a quick note assuring that your needs are being
#     addressed.
#   
#     I want you to know that my team will keep working on the issues,
#     especially:
#   
#     <%# ignore numerous minor requests -- focus on priorities %>
#     % priorities.each do |priority|
#       * <%= priority %>
#     % end
#   
#     Thanks for your patience.
#   
#     James Edward Gray II
#   }.gsub(/^  /, '')
#   
#   message = ERB.new(template, 0, "%<>")
#   
#   # Set up template data.
#   to = "Community Spokesman <spokesman@ruby_community.org>"
#   priorities = [ "Run Ruby Quiz",
#                  "Document Modules",
#                  "Answer Questions on Ruby Talk" ]
#   
#   # Produce result.
#   email = message.result
#   puts email
#
# <i>Generates:</i>
#
#   From:  James Edward Gray II <james@grayproductions.net>
#   To:  Community Spokesman <spokesman@ruby_community.org>
#   Subject:  Addressing Needs
#   
#   Community:
#   
#   Just wanted to send a quick note assuring that your needs are being addressed.
#   
#   I want you to know that my team will keep working on the issues, especially:
#   
#       * Run Ruby Quiz
#       * Document Modules
#       * Answer Questions on Ruby Talk
#   
#   Thanks for your patience.
#   
#   James Edward Gray II
#
# === Ruby in HTML
#
# ERB is often used in <tt>.rhtml</tt> files (HTML with embedded Ruby).  Notice the need in
# this example to provide a special binding when the template is run, so that the instance
# variables in the Product object can be resolved.
#
#   require "erb"
#   
#   # Build template data class.
#   class Product
#     def initialize( code, name, desc, cost )
#       @code = code
#       @name = name
#       @desc = desc
#       @cost = cost
#        	
#       @features = [ ]
#     end
#   
#     def add_feature( feature )
#       @features << feature
#     end
#   
#     # Support templating of member data.
#     def get_binding
#       binding
#     end
#   
#     # ...
#   end
#   
#   # Create template.
#   template = %{
#     <html>
#       <head><title>Ruby Toys -- <%= @name %></title></head>
#       <body>
#   
#         <h1><%= @name %> (<%= @code %>)</h1>
#         <p><%= @desc %></p>
#   
#         <ul>
#           <% @features.each do |f| %>
#             <li><b><%= f %></b></li>
#           <% end %>
#         </ul>
#   
#         <p>
#           <% if @cost < 10 %>
#             <b>Only <%= @cost %>!!!</b>
#           <% else %>
#              Call for a price, today!
#           <% end %>
#         </p>
#    
#       </body>
#     </html>
#   }.gsub(/^  /, '')
#   
#   rhtml = ERB.new(template)
#   
#   # Set up template data.
#   toy = Product.new( "TZ-1002",
#                      "Rubysapien",
#                      "Geek's Best Friend!  Responds to Ruby commands...",
#                      999.95 )
#   toy.add_feature("Listens for verbal commands in the Ruby language!")
#   toy.add_feature("Ignores Perl, Java, and all C variants.")
#   toy.add_feature("Karate-Chop Action!!!")
#   toy.add_feature("Matz signature on left leg.")
#   toy.add_feature("Gem studded eyes... Rubies, of course!")
#   
#   # Produce result.
#   rhtml.run(toy.get_binding)
#
# <i>Generates (some blank lines removed):</i>
#
#    <html>
#      <head><title>Ruby Toys -- Rubysapien</title></head>
#      <body>
#    
#        <h1>Rubysapien (TZ-1002)</h1>
#        <p>Geek's Best Friend!  Responds to Ruby commands...</p>
#    
#        <ul>
#            <li><b>Listens for verbal commands in the Ruby language!</b></li>
#            <li><b>Ignores Perl, Java, and all C variants.</b></li>
#            <li><b>Karate-Chop Action!!!</b></li>
#            <li><b>Matz signature on left leg.</b></li>
#            <li><b>Gem studded eyes... Rubies, of course!</b></li>
#        </ul>
#    
#        <p>
#             Call for a price, today!
#        </p>
#    
#      </body>
#    </html>
#
# 
# == Notes
#
# There are a variety of templating solutions available in various Ruby projects:
# * ERB's big brother, eRuby, works the same but is written in C for speed;
# * Amrita (smart at producing HTML/XML);
# * cs/Template (written in C for speed);
# * RDoc, distributed with Ruby, uses its own template engine, which can be reused elsewhere;
# * and others; search the RAA.
#
# Rails, the web application framework, uses ERB to create views.
#
class ERB
  Revision = '$Date: 2006/06/10 19:14:00 $' 	#'

  # Returns revision information for the erb.rb module.
  def self.version
    "erb.rb [2.0.4 #{ERB::Revision.split[1]}]"
  end
end

#--
# ERB::Compiler
class ERB
  class Compiler # :nodoc:
    class PercentLine # :nodoc:
      def initialize(str)
        @value = str
      end
      attr_reader :value
      alias :to_s :value
    end

    class Scanner # :nodoc:
      SplitRegexp = /(<%%)|(%%>)|(<%=)|(<%#)|(<%)|(%>)|(\n)/

      @scanner_map = {}
      def self.regist_scanner(klass, trim_mode, percent)
	@scanner_map[[trim_mode, percent]] = klass
      end

      def self.default_scanner=(klass)
	@default_scanner = klass
      end

      def self.make_scanner(src, trim_mode, percent)
	klass = @scanner_map.fetch([trim_mode, percent], @default_scanner)
	klass.new(src, trim_mode, percent)
      end

      def initialize(src, trim_mode, percent)
	@src = src
	@stag = nil
      end
      attr_accessor :stag

      def scan; end
    end

    class TrimScanner < Scanner # :nodoc:
      TrimSplitRegexp = /(<%%)|(%%>)|(<%=)|(<%#)|(<%)|(%>\n)|(%>)|(\n)/

      def initialize(src, trim_mode, percent)
	super
	@trim_mode = trim_mode
	@percent = percent
	if @trim_mode == '>'
	  @scan_line = self.method(:trim_line1)
	elsif @trim_mode == '<>'
	  @scan_line = self.method(:trim_line2)
	elsif @trim_mode == '-'
	  @scan_line = self.method(:explicit_trim_line)
	else
	  @scan_line = self.method(:scan_line)
	end
      end
      attr_accessor :stag
      
      def scan(&block)
	@stag = nil
	if @percent
	  @src.each do |line|
	    percent_line(line, &block)
	  end
	else
	  @src.each do |line|
	    @scan_line.call(line, &block)
	  end
	end
	nil
      end

      def percent_line(line, &block)
	if @stag || line[0] != ?%
	  return @scan_line.call(line, &block)
	end

	line[0] = ''
	if line[0] == ?%
	  @scan_line.call(line, &block)
	else
          yield(PercentLine.new(line.chomp))
	end
      end

      def scan_line(line)
	line.split(SplitRegexp).each do |token|
	  next if token.empty?
	  yield(token)
	end
      end

      def trim_line1(line)
	line.split(TrimSplitRegexp).each do |token|
	  next if token.empty?
	  if token == "%>\n"
	    yield('%>')
	    yield(:cr)
	    break
	  end
	  yield(token)
	end
      end

      def trim_line2(line)
	head = nil
	line.split(TrimSplitRegexp).each do |token|
	  next if token.empty?
	  head = token unless head
	  if token == "%>\n"
	    yield('%>')
	    if  is_erb_stag?(head)
	      yield(:cr)
	    else
	      yield("\n")
	    end
	    break
	  end
	  yield(token)
	end
      end

      ExplicitTrimRegexp = /(^[ \t]*<%-)|(-%>\n?\z)|(<%-)|(-%>)|(<%%)|(%%>)|(<%=)|(<%#)|(<%)|(%>)|(\n)/
      def explicit_trim_line(line)
	line.split(ExplicitTrimRegexp).each do |token|
	  next if token.empty?
	  if @stag.nil? && /[ \t]*<%-/ =~ token
	    yield('<%')
	  elsif @stag && /-%>\n/ =~ token
	    yield('%>')
	    yield(:cr)
	  elsif @stag && token == '-%>'
	    yield('%>')
	  else
	    yield(token)
	  end
	end
      end

      ERB_STAG = %w(<%= <%# <%)
      def is_erb_stag?(s)
	ERB_STAG.member?(s)
      end
    end

    Scanner.default_scanner = TrimScanner

    class SimpleScanner < Scanner # :nodoc:
      def scan
	@src.each do |line|
	  line.split(SplitRegexp).each do |token|
	    next if token.empty?
	    yield(token)
	  end
	end
      end
    end
    
    Scanner.regist_scanner(SimpleScanner, nil, false)

    begin
      require 'strscan'
      class SimpleScanner2 < Scanner # :nodoc:
        def scan
          stag_reg = /(.*?)(<%%|<%=|<%#|<%|\n|\z)/
          etag_reg = /(.*?)(%%>|%>|\n|\z)/
          scanner = StringScanner.new(@src)
          while ! scanner.eos?
            scanner.scan(@stag ? etag_reg : stag_reg)
            text = scanner[1]
            elem = scanner[2]
            yield(text) unless text.empty?
            yield(elem) unless elem.empty?
          end
        end
      end
      Scanner.regist_scanner(SimpleScanner2, nil, false)

      class PercentScanner < Scanner # :nodoc:
	def scan
	  new_line = true
          stag_reg = /(.*?)(<%%|<%=|<%#|<%|\n|\z)/
          etag_reg = /(.*?)(%%>|%>|\n|\z)/
          scanner = StringScanner.new(@src)
          while ! scanner.eos?
	    if new_line && @stag.nil?
	      if scanner.scan(/%%/)
		yield('%')
		new_line = false
		next
	      elsif scanner.scan(/%/)
		yield(PercentLine.new(scanner.scan(/.*?(\n|\z)/).chomp))
		next
	      end
	    end
	    scanner.scan(@stag ? etag_reg : stag_reg)
            text = scanner[1]
            elem = scanner[2]
            yield(text) unless text.empty?
            yield(elem) unless elem.empty?
	    new_line = (elem == "\n")
          end
        end
      end
      Scanner.regist_scanner(PercentScanner, nil, true)

      class ExplicitScanner < Scanner # :nodoc:
	def scan
	  new_line = true
          stag_reg = /(.*?)(<%%|<%=|<%#|<%-|<%|\n|\z)/
          etag_reg = /(.*?)(%%>|-%>|%>|\n|\z)/
          scanner = StringScanner.new(@src)
          while ! scanner.eos?
	    if new_line && @stag.nil? && scanner.scan(/[ \t]*<%-/)
	      yield('<%')
	      new_line = false
	      next
	    end
	    scanner.scan(@stag ? etag_reg : stag_reg)
            text = scanner[1]
            elem = scanner[2]
	    new_line = (elem == "\n")
            yield(text) unless text.empty?
	    if elem == '-%>'
	      yield('%>')
	      if scanner.scan(/(\n|\z)/)
		yield(:cr)
		new_line = true
	      end
	    elsif elem == '<%-'
	      yield('<%')
	    else
	      yield(elem) unless elem.empty?
	    end
          end
        end
      end
      Scanner.regist_scanner(ExplicitScanner, '-', false)

    rescue LoadError
    end

    class Buffer # :nodoc:
      def initialize(compiler)
	@compiler = compiler
	@line = []
	@script = ""
	@compiler.pre_cmd.each do |x|
	  push(x)
	end
      end
      attr_reader :script

      def push(cmd)
	@line << cmd
      end
      
      def cr
	@script << (@line.join('; '))
	@line = []
	@script << "\n"
      end
      
      def close
	return unless @line
	@compiler.post_cmd.each do |x|
	  push(x)
	end
	@script << (@line.join('; '))
	@line = nil
      end
    end

    def compile(s)
      out = Buffer.new(self)

      content = ''
      scanner = make_scanner(s)
      scanner.scan do |token|
	if scanner.stag.nil?
	  case token
          when PercentLine
	    out.push("#{@put_cmd} #{content.dump}") if content.size > 0
	    content = ''
            out.push(token.to_s)
            out.cr
	  when :cr
	    out.cr
	  when '<%', '<%=', '<%#'
	    scanner.stag = token
	    out.push("#{@put_cmd} #{content.dump}") if content.size > 0
	    content = ''
	  when "\n"
	    content << "\n"
	    out.push("#{@put_cmd} #{content.dump}")
	    out.cr
	    content = ''
	  when '<%%'
	    content << '<%'
	  else
	    content << token
	  end
	else
	  case token
	  when '%>'
	    case scanner.stag
	    when '<%'
	      if content[-1] == ?\n
		content.chop!
		out.push(content)
		out.cr
	      else
		out.push(content)
	      end
	    when '<%='
	      out.push("#{@put_cmd}((#{content}).to_s)")
	    when '<%#'
	      # out.push("# #{content.dump}")
	    end
	    scanner.stag = nil
	    content = ''
	  when '%%>'
	    content << '%>'
	  else
	    content << token
	  end
	end
      end
      out.push("#{@put_cmd} #{content.dump}") if content.size > 0
      out.close
      out.script
    end

    def prepare_trim_mode(mode)
      case mode
      when 1
	return [false, '>']
      when 2
	return [false, '<>']
      when 0
	return [false, nil]
      when String
	perc = mode.include?('%')
	if mode.include?('-')
	  return [perc, '-']
	elsif mode.include?('<>')
	  return [perc, '<>']
	elsif mode.include?('>')
	  return [perc, '>']
	else
	  [perc, nil]
	end
      else
	return [false, nil]
      end
    end

    def make_scanner(src)
      Scanner.make_scanner(src, @trim_mode, @percent)
    end

    def initialize(trim_mode)
      @percent, @trim_mode = prepare_trim_mode(trim_mode)
      @put_cmd = 'print'
      @pre_cmd = []
      @post_cmd = []
    end
    attr_reader :percent, :trim_mode
    attr_accessor :put_cmd, :pre_cmd, :post_cmd
  end
end

#--
# ERB
class ERB
  #
  # Constructs a new ERB object with the template specified in _str_.
  # 
  # An ERB object works by building a chunk of Ruby code that will output
  # the completed template when run. If _safe_level_ is set to a non-nil value,
  # ERB code will be run in a separate thread with <b>$SAFE</b> set to the
  # provided level.
  # 
  # If _trim_mode_ is passed a String containing one or more of the following
  # modifiers, ERB will adjust its code generation as listed:
  # 
  # 	%  enables Ruby code processing for lines beginning with %
  # 	<> omit newline for lines starting with <% and ending in %>
  # 	>  omit newline for lines ending in %>
  # 
  # _eoutvar_ can be used to set the name of the variable ERB will build up
  # its output in.  This is useful when you need to run multiple ERB
  # templates through the same binding and/or when you want to control where
  # output ends up.  Pass the name of the variable to be used inside a String.
  #
  # === Example
  #
  #  require "erb"
  #  
  #  # build data class
  #  class Listings
  #    PRODUCT = { :name => "Chicken Fried Steak",
  #                :desc => "A well messages pattie, breaded and fried.",
  #                :cost => 9.95 }
  #  
  #    attr_reader :product, :price
  #    
  #    def initialize( product = "", price = "" )
  #      @product = product
  #      @price = price
  #    end
  #    
  #    def build
  #      b = binding
  #      # create and run templates, filling member data variebles
  #      ERB.new(<<-'END_PRODUCT'.gsub(/^\s+/, ""), 0, "", "@product").result b
  #        <%= PRODUCT[:name] %>
  #        <%= PRODUCT[:desc] %>
  #      END_PRODUCT
  #      ERB.new(<<-'END_PRICE'.gsub(/^\s+/, ""), 0, "", "@price").result b
  #        <%= PRODUCT[:name] %> -- <%= PRODUCT[:cost] %>
  #        <%= PRODUCT[:desc] %>
  #      END_PRICE
  #    end
  #  end
  #  
  #  # setup template data
  #  listings = Listings.new
  #  listings.build
  #  
  #  puts listings.product + "\n" + listings.price
  #
  # _Generates_
  #
  #  Chicken Fried Steak
  #  A well messages pattie, breaded and fried.
  #  
  #  Chicken Fried Steak -- 9.95
  #  A well messages pattie, breaded and fried.
  #  
  def initialize(str, safe_level=nil, trim_mode=nil, eoutvar='_erbout')
    @safe_level = safe_level
    compiler = ERB::Compiler.new(trim_mode)
    set_eoutvar(compiler, eoutvar)
    @src = compiler.compile(str)
    @filename = nil
  end

  # The Ruby code generated by ERB
  attr_reader :src

  # The optional _filename_ argument passed to Kernel#eval when the ERB code
  # is run
  attr_accessor :filename

  #
  # Can be used to set _eoutvar_ as described in ERB#new.  It's probably easier
  # to just use the constructor though, since calling this method requires the
  # setup of an ERB _compiler_ object.
  #
  def set_eoutvar(compiler, eoutvar = '_erbout')
    compiler.put_cmd = "#{eoutvar}.concat"

    cmd = []
    cmd.push "#{eoutvar} = ''"
    
    compiler.pre_cmd = cmd

    cmd = []
    cmd.push(eoutvar)

    compiler.post_cmd = cmd
  end

  # Generate results and print them. (see ERB#result)
  def run(b=TOPLEVEL_BINDING)
    print self.result(b)
  end

  #
  # Executes the generated ERB code to produce a completed template, returning
  # the results of that code.  (See ERB#new for details on how this process can
  # be affected by _safe_level_.)
  # 
  # _b_ accepts a Binding or Proc object which is used to set the context of
  # code evaluation.
  #
  def result(b=TOPLEVEL_BINDING)
    if @safe_level
      th = Thread.start { 
	$SAFE = @safe_level
	eval(@src, b, (@filename || '(erb)'), 1)
      }
      return th.value
    else
      return eval(@src, b, (@filename || '(erb)'), 1)
    end
  end

  def def_method(mod, methodname, fname='(ERB)')  # :nodoc:
    mod.module_eval("def #{methodname}\n" + self.src + "\nend\n", fname, 0)
  end

  def def_module(methodname='erb')  # :nodoc:
    mod = Module.new
    def_method(mod, methodname)
    mod
  end

  def def_class(superklass=Object, methodname='result')  # :nodoc:
    cls = Class.new(superklass)
    def_method(cls, methodname)
    cls
  end
end

#--
# ERB::Util
class ERB
  # A utility module for conversion routines, often handy in HTML generation.
  module Util
    public
    #
    # A utility method for escaping HTML tag characters in _s_.
    # 
    # 	require "erb"
    # 	include ERB::Util
    # 	
    # 	puts html_escape("is a > 0 & a < 10?")
    # 
    # _Generates_
    # 
    # 	is a &gt; 0 &amp; a &lt; 10?
    #
    def html_escape(s)
      s.to_s.gsub(/&/, "&amp;").gsub(/\"/, "&quot;").gsub(/>/, "&gt;").gsub(/</, "&lt;")
    end
    alias h html_escape
    module_function :h
    module_function :html_escape
    
    #
    # A utility method for encoding the String _s_ as a URL.
    # 
    # 	require "erb"
    # 	include ERB::Util
    # 	
    # 	puts url_encode("Programming Ruby:  The Pragmatic Programmer's Guide")
    # 
    # _Generates_
    # 
    # 	Programming%20Ruby%3A%20%20The%20Pragmatic%20Programmer%27s%20Guide
    #
    def url_encode(s)
      s.to_s.gsub(/[^a-zA-Z0-9_\-.]/n){ sprintf("%%%02X", $&.unpack("C")[0]) }
    end
    alias u url_encode
    module_function :u
    module_function :url_encode
  end
end

#--
# ERB::DefMethod
class ERB
  module DefMethod  # :nodoc:
    public
    def def_erb_method(methodname, erb)
      if erb.kind_of? String
	fname = erb
	File.open(fname) {|f| erb = ERB.new(f.read) }
	erb.def_method(self, methodname, fname)
      else
	erb.def_method(self, methodname)
      end
    end
    module_function :def_erb_method
  end
end



#
#   math-mode.rb - 
#   	$Release Version: 0.9.5$
#   	$Revision: 1.2 $
#   	$Date: 2006/06/10 19:14:07 $
#   	by Keiju ISHITSUKA(keiju@ruby-lang.org)
#
# --
#
#   
#
require "mathn"

module IRB
  class Context
    attr_reader :math_mode
    alias math? math_mode

    def math_mode=(opt)
      if @math_mode == true && opt == false
	IRB.fail CantReturnToNormalMode
	return
      end

      @math_mode = opt
      if math_mode
	main.extend Math
	print "start math mode\n" if verbose?
      end
    end

    def inspect?
      @inspect_mode.nil? && !@math_mode or @inspect_mode
    end
  end
end


#
#   change-ws.rb - 
#   	$Release Version: 0.9.5$
#   	$Revision: 1.2 $
#   	$Date: 2006/06/10 19:14:10 $
#   	by Keiju ISHITSUKA(keiju@ruby-lang.org)
#
# --
#
#   
#

require "irb/cmd/nop.rb"
require "irb/ext/change-ws.rb"

module IRB
  module ExtendCommand

    class CurrentWorkingWorkspace<Nop
      def execute(*obj)
	irb_context.main
      end
    end

    class ChangeWorkspace<Nop
      def execute(*obj)
	irb_context.change_workspace(*obj)
	irb_context.main
      end
    end
  end
end


#
#   shell/error.rb - 
#   	$Release Version: 0.6.0 $
#   	$Revision: 1.2 $
#   	$Date: 2006/06/10 19:14:06 $
#   	by Keiju ISHITSUKA(Nihon Rational Software Co.,Ltd)
#
# --
#
#   
#

require "e2mmap"

class Shell
  module Error
    extend Exception2MessageMapper
    def_e2message TypeError, "wrong argument type %s (expected %s)"

    def_exception :DirStackEmpty, "Directory stack empty."
    def_exception :CantDefine, "Can't define method(%s, %s)."
    def_exception :CantApplyMethod, "This method(%s) does not apply to this type(%s)."
    def_exception :CommandNotFound, "Command not found(%s)."
  end
end


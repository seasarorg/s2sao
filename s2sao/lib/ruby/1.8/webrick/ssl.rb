#
# ssl.rb -- SSL/TLS enhancement for GenericServer
#
# Copyright (c) 2003 GOTOU Yuuzou All rights reserved.
# 
# $Id: ssl.rb,v 1.2 2006/06/10 19:14:05 headius Exp $

require 'webrick'
require 'openssl'

module WEBrick
  module Config
    svrsoft = General[:ServerSoftware]
    osslv = ::OpenSSL::OPENSSL_VERSION.split[1]
    SSL = {
      :ServerSoftware       => "#{svrsoft} OpenSSL/#{osslv}",
      :SSLEnable            => false,
      :SSLCertificate       => nil,
      :SSLPrivateKey        => nil,
      :SSLClientCA          => nil,
      :SSLExtraChainCert    => nil,
      :SSLCACertificateFile => nil,
      :SSLCACertificatePath => nil,
      :SSLCertificateStore  => nil,
      :SSLVerifyClient      => ::OpenSSL::SSL::VERIFY_NONE,
      :SSLVerifyDepth       => nil,
      :SSLVerifyCallback    => nil,   # custom verification
      :SSLTimeout           => nil,
      :SSLOptions           => nil,
      :SSLStartImmediately  => true,
      # Must specify if you use auto generated certificate.
      :SSLCertName          => nil,
      :SSLCertComment       => "Generated by Ruby/OpenSSL"
    }
    General.update(SSL)
  end

  module Utils
    def create_self_signed_cert(bits, cn, comment)
      rsa = OpenSSL::PKey::RSA.new(bits){|p, n|
        case p
        when 0; $stderr.putc "."  # BN_generate_prime
        when 1; $stderr.putc "+"  # BN_generate_prime
        when 2; $stderr.putc "*"  # searching good prime,  
                                  # n = #of try,
                                  # but also data from BN_generate_prime
        when 3; $stderr.putc "\n" # found good prime, n==0 - p, n==1 - q,
                                  # but also data from BN_generate_prime
        else;   $stderr.putc "*"  # BN_generate_prime
        end
      }
      cert = OpenSSL::X509::Certificate.new
      cert.version = 3
      cert.serial = 0
      name = OpenSSL::X509::Name.new(cn)
      cert.subject = name
      cert.issuer = name
      cert.not_before = Time.now
      cert.not_after = Time.now + (365*24*60*60)
      cert.public_key = rsa.public_key

      ef = OpenSSL::X509::ExtensionFactory.new(nil,cert)
      ef.issuer_certificate = cert
      cert.extensions = [
        ef.create_extension("basicConstraints","CA:FALSE"),
        ef.create_extension("keyUsage", "keyEncipherment"),
        ef.create_extension("subjectKeyIdentifier", "hash"),
        ef.create_extension("extendedKeyUsage", "serverAuth"),
        ef.create_extension("nsComment", comment),
      ]
      aki = ef.create_extension("authorityKeyIdentifier",
                                "keyid:always,issuer:always")
      cert.add_extension(aki)
      cert.sign(rsa, OpenSSL::Digest::SHA1.new)

      return [ cert, rsa ]
    end
    module_function :create_self_signed_cert
  end

  class GenericServer
    def ssl_context
      @ssl_context ||= nil
    end

    def listen(address, port)
      listeners = Utils::create_listeners(address, port, @logger)
      if @config[:SSLEnable]
        unless ssl_context
          @ssl_context = setup_ssl_context(@config)
          @logger.info("\n" + @config[:SSLCertificate].to_text) 
        end
        listeners.collect!{|svr|
          ssvr = ::OpenSSL::SSL::SSLServer.new(svr, ssl_context)
          ssvr.start_immediately = @config[:SSLStartImmediately]
          ssvr
        }
      end
      @listeners += listeners
    end

    def setup_ssl_context(config)
      unless config[:SSLCertificate]
        cn = config[:SSLCertName]
        comment = config[:SSLCertComment]
        cert, key = Utils::create_self_signed_cert(1024, cn, comment)
        config[:SSLCertificate] = cert
        config[:SSLPrivateKey] = key
      end
      ctx = OpenSSL::SSL::SSLContext.new
      ctx.key = config[:SSLPrivateKey]
      ctx.cert = config[:SSLCertificate]
      ctx.client_ca = config[:SSLClientCA]
      ctx.extra_chain_cert = config[:SSLExtraChainCert]
      ctx.ca_file = config[:SSLCACertificateFile]
      ctx.ca_path = config[:SSLCACertificatePath]
      ctx.cert_store = config[:SSLCertificateStore]
      ctx.verify_mode = config[:SSLVerifyClient]
      ctx.verify_depth = config[:SSLVerifyDepth]
      ctx.verify_callback = config[:SSLVerifyCallback]
      ctx.timeout = config[:SSLTimeout]
      ctx.options = config[:SSLOptions]
      ctx
    end
  end
end

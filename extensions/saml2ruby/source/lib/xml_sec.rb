# The contents of this file are subject to the terms
# of the Common Development and Distribution License
# (the License). You may not use this file except in
# compliance with the License.
#
# You can obtain a copy of the License at
# https://opensso.dev.java.net/public/CDDLv1.0.html or
# opensso/legal/CDDLv1.0.txt
# See the License for the specific language governing
# permission and limitations under the License.
#
# When distributing Covered Code, include this CDDL
# Header Notice in each file and include the License file
# at opensso/legal/CDDLv1.0.txt.
# If applicable, add the following below the CDDL Header,
# with the fields enclosed by brackets [] replaced by
# your own identifying information:
# "Portions Copyrighted [year] [name of copyright owner]"
#
# $Id: xml_sec.rb,v 1.3 2007-06-02 07:56:25 todddd Exp $
#
# Copyright 2007 Sun Microsystems Inc. All Rights Reserved
# Portions Copyrighted 2007 Todd W Saxton.

require "rexml/document"
require "rexml/xpath"
require "openssl"
require "xmlcanonicalizer"
require "digest/sha1"
 
#
# WARNING, WARNING: VERY rudimentary
#
#
module XMLSecurity

  class SignedDocument < REXML::Document

    def validate(idp_cert_fingerprint, logger)
        
      # get cert from response
      base64_cert = self.elements["//X509Certificate"].text
      cert_text = Base64.decode64(base64_cert)
      cert = OpenSSL::X509::Certificate.new(cert_text)
      
      # check cert matches registered idp cert
      fingerprint = Digest::SHA1.hexdigest(cert.to_der)
      logger.info("fingerprint = " + fingerprint) if !logger.nil?
      valid_flag = fingerprint == idp_cert_fingerprint.gsub(":", "").downcase
      
      return valid_flag if !valid_flag 
       
      #        
      #validate references
      #
      signed_info_element = self.elements["//SignedInfo"]
      
      # remove signature node
      self.elements["//Signature"].remove
      
      #check digests
      signed_info_element.elements.each("//Reference") do | ref |          
        uri = ref.attributes.get_attribute("URI").value
        logger.info("URI = " + uri[1,uri.size]) if !logger.nil?
        self.root.each_element_with_attribute("ID", uri[1,uri.size]) do | signed_element |
          logger.info("signed element = " + signed_element.to_s) if !logger.nil?
          canoner = XML::Util::XmlCanonicalizer.new(false, true)
          canon_signed_element = canoner.canonicalize(signed_element)
          logger.info("canon signed element = " + canon_signed_element) if !logger.nil?
          signed_element_hash = Base64.encode64(Digest::SHA1.digest(canon_signed_element)).chomp
          digest_value = ref.elements["//DigestValue"].text
          logger.info("signed_element_hash = " + signed_element_hash) if !logger.nil?
          logger.info("digest_value_element = " + digest_value) if !logger.nil?
          
          valid_flag = signed_element_hash == digest_value 
          return valid_flag if !valid_flag
        end
      end
 
      #verify dig sig          
      signed_info_element.add_namespace("http://www.w3.org/2000/09/xmldsig#")
      canoner = XML::Util::XmlCanonicalizer.new(false, true)
      canon_signed_info = canoner.canonicalize(signed_info_element)
      logger.info("canon INFO = " + canon_signed_info) if !logger.nil?

      base64_signature = signed_info_element.elements["//SignatureValue"].text.gsub("\n","")
      logger.info("base 64 SIG = " + base64_signature) if !logger.nil?
      signature = Base64.decode64(base64_signature)
      logger.info("SIG = " + signature) if !logger.nil?
      
      valid_flag = cert.public_key.verify(OpenSSL::Digest::SHA1.new, signature, canon_signed_info)
        
      return valid_flag
    end
   
  end
end

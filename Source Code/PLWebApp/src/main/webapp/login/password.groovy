import groovy.sql.Sql
import org.plweb.webapp.helper.CommonHelper
import java.text.DecimalFormat
import net.tanesha.recaptcha.ReCaptcha
import net.tanesha.recaptcha.ReCaptchaFactory

helper = new CommonHelper(request, response, session)

//產生圖片驗證碼
helper.attr 'recaptcha_public_key', "6LfVocMSAAAAANwnuKgRfnnMEv6Fx8yU-h_X53xl"

helper.include 'password.gsp'
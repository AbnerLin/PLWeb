(function($){
        $.fn.ipaddress = function(params) {
                return $(this).each(function(){
                        var $this = $(this);
                        var options = $.extend({
                                cidr : false
                        }, params);

                        if (!$this.hasClass('ip-enabled')) {
                                $this.hide();

                                var ip_split = (this.value) ? this.value.split('/') : ['...','32'];

                                var ip_cidr = ip_split[1] || '32';

                                var ip_value = ip_split[0].split('.');

                                var octets = [];
                                var id_prefix = $this.attr('name').replace(/\[/g, '_').replace(/\]/g, '');
                                for (var i = 0; i <=3; i++) {
                                        octets.push('<input type="text" class="ip_octet" id="' + id_prefix + '_octet_' + (i+1) + '" maxlength="3" value="' + ip_value[i] + '" />');
                                }
                                var octet_html = octets.join('.');
                                if (options.cidr) {
                                        octet_html += '/<input type="text" class="ip_cidr ip_octet" id="' + id_prefix + '_octet_cidr" maxlength="2" value="' + ip_cidr + '" />';
                                }

                                $this.after($('<div class="ip_container" style="display: inline' + (($.browser.msie) ? '' : '-block') + ';"/>').html(octet_html));
                                $this.addClass('ip-enabled');
                        }

                        var isNumeric = function(e){
                                if (e.shiftKey) return false;
                                return (e.keyCode >= 48 && e.keyCode <= 57) || (e.keyCode >= 96 && e.keyCode <= 105);
                        };

                        var isValidKey = function(e){
                                var valid = [
                                        8,        // backspace
                                        9,        // tab
                                        13,       // enter
                                        27,       // escape
                                        35,       // end
                                        36,       // home
                                        37,       // left arrow
                                        39,       // right arrow
                                        46,       // delete
                                        48, 96,   // 0
                                        49, 97,   // 1
                                        50, 98,   // 2
                                        51, 99,   // 3
                                        52, 100,  // 4
                                        53, 101,  // 5
                                        54, 102,  // 6
                                        55, 103,  // 7
                                        56, 104,  // 8
                                        57, 105,  // 9
                                        110, 190  // period
                                ];

                                if (options.cidr) {
                                        valid.push(111, 191); // slash
                                }

                                // only allow shift key with tab
                                if (e.shiftKey && e.keyCode != 9) return false;

                                for (var i = 0, c; c = valid[i]; i++) {
                                        if (e.keyCode == c) return true;
                                }

                                return false;
                        };

                        var saveIP = function(el) {
                                // save value to original input if all octets have been entered
                                if ($('input.ip_octet:not(.ip_cidr)', $(el).parent()).filter(function(){ return this.value.length; }).length == 4) {
                                        var ip_value = [], ip = '', cidr = '';
                                        $('input.ip_octet:not(.ip_cidr)', $(el).parent()).each(function(){
                                                ip_value.push(this.value);
                                        });
                                        ip = ip_value.join('.');

                                        if (options.cidr) {
                                                var $cidr = $('input.ip_cidr', $(el).parent());
                                                cidr = ($cidr.length) ? '/' + $cidr.val() : '/32';
                                        }

                                        $this.val(ip + cidr);
                                }
                                else {
                                        $this.val('');
                                }
                        }

                        $('input.ip_octet').bind('keydown', function(e){
                                if (!isValidKey(e)) return false;

                                var next_octet = $(this).next('input.ip_octet');
                                var prev_octet = $(this).prev('input.ip_octet');

                                // jump to next octet on period if this octet has a value
                                if (e.keyCode == 110 || e.keyCode == 190) {
                                        if (this.value.length) {
                                                if (next_octet.length) {
                                                        next_octet.focus();
                                                        next_octet.select();
                                                }
                                        }
                                        return false;
                                }

                                // set empty octets to zero and jump to cidr input on slash
                                if ((e.keyCode == 111 || e.keyCode == 191) && options.cidr) {
                                        $('input.ip_octet:not(.ip_cidr)', $(this).parent()).filter(function(){ return !this.value.length; }).val('0');
                                        $('input.ip_cidr', $(this).parent()).focus();
                                        return false;
                                }

                                if (($(this).caret()[1] - $(this).caret()[0]) && isNumeric(e)) {
                                        return true;
                                }

                                // jump to next octet if maxlength is reached and number key or right arrow is pressed
                                if ((this.value.length == this.getAttribute('maxlength') && $(this).caret()[0] == this.getAttribute('maxlength') && (isNumeric(e) || e.keyCode == 39)) || (e.keyCode == 39 && $(this).caret()[0] == this.value.length)) {
                                        if (next_octet.length) {
                                                $(this).trigger('blur');
                                                next_octet.focus().caret(0);
                                                return true;
                                        }
                                }

                                // jump to previous octet if left arrow is pressed and caret is at the 0 position
                                if (e.keyCode == 37 && $(this).caret()[0] == 0) {
                                        if (prev_octet.length) {
                                                $(this).trigger('blur');
                                                prev_octet.caret(prev_octet.val().length);
                                                return false;
                                        }
                                }

                                // jump to previous octet on backspace
                                if (e.keyCode == 8 && $(this).caret()[0] == 0 && $(this).caret()[0] == $(this).caret()[1]) {
                                        if (prev_octet.length) {
                                                $(this).trigger('blur');
                                                prev_octet.focus().caret(prev_octet.val().length);
                                                return false;
                                        }
                                }
                }).bind('keyup', function(e){
                                if (this.value > 255) this.value = 255;

                                saveIP(this);
                        });

                        $('input.ip_cidr').bind('keyup', function(e){
                                if (this.value > 32) this.value = 32;

                                saveIP(this);
                        });

                });

        };
})(jQuery);


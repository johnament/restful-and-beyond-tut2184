/*******************************************************************************
 * Copyright (c) 2013 - 2014 Sparta Systems, Inc.
 ******************************************************************************/

/*
 This module splits a long slide into multiple slides.
 */
(function($, deck, undefined) {
    var $d = $(document);

    createEmptySlide = function(title_el) {
        slide = $(document.createElement('section'));
        slide.addClass('slide').append(title_el.clone());
        return slide;
    };

    $d.bind('deck.beforeInit', function() {
        $(".slide").each( function(i, slide) {
            /* each slide */
            var is_split = false,
                prev_slide = $(slide),
            /* extract title which will be added to each new slides */
                title_el = prev_slide.children('h2'),
                tmp_slide = createEmptySlide(title_el);

            $(slide).children().each(function() {
                /* for each element inside original slide */
                var el = $(this);

                if (el.css('page-break-after') == 'always') {
                    if (is_split) {
                        tmp_slide.insertAfter(prev_slide);
                        prev_slide = tmp_slide;
                        tmp_slide = createEmptySlide(title_el);
                    }
                    else {
                        /* find the first page break */
                        is_split = true;
                    }
                }
                else {
                    if (is_split) {
                        tmp_slide.append(el);
                    }
                }
            })
            /* add remaining elements to a new slides */
            if (is_split) {
                tmp_slide.insertAfter(prev_slide);
            }
        });
    })
})(jQuery, 'deck');

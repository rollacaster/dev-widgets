(defun dev-widget ()
  (interactive)
  (cider-interactive-eval
   (let ((position (window-absolute-pixel-position)))
     (concat "(dev-widgets.desktop-widget.core/-main "
             "\""
             buffer-file-name
             "\""
             " "
             (number-to-string (point))
             " "
             "\["
             (number-to-string (first position))
             " "
             (number-to-string (rest position))
             "\]"
             ")"))))

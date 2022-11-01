(defun dev-widget (&optional additional-params)
  (interactive)
  (let ((form  (let ((position (window-absolute-pixel-position)))
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
    (cider-map-repls :clj
      (lambda (connection)
        (cider--prep-interactive-eval form connection)
        (cider-nrepl-request:eval
         form
         (cider-interactive-eval-handler nil nil)
         ;; always eval ns forms in the user namespace
         ;; otherwise trying to eval ns form for the first time will produce an error
         (if (cider-ns-form-p form) "user" (cider-current-ns))
         nil
         nil
         (seq-mapcat #'identity additional-params)
         connection)))))
